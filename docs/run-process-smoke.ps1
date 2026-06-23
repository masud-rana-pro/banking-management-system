$ErrorActionPreference = 'Stop'

$BaseUrl = 'http://localhost:8080/api'
$ArtifactRoot = Join-Path 'I:\SBMS Copy\docs' ('smoke-artifacts-' + (Get-Date -Format 'yyyyMMdd-HHmmss'))
New-Item -ItemType Directory -Path $ArtifactRoot -Force | Out-Null

$Results = New-Object System.Collections.Generic.List[object]
$Tokens = @{}
$Created = @{}

function Get-MySqlScalar {
    param([string]$Sql)

    $env:MYSQL_PWD = 'root'
    $lines = & 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe' --batch --skip-column-names -uroot -D sbms -e $Sql 2>$null
    Remove-Item Env:MYSQL_PWD -ErrorAction SilentlyContinue

    return @($lines | Select-Object -First 1)[0]
}

$MonthlyClosingMonth = $null
for ($i = 1; $i -le 12; $i++) {
    $candidate = (Get-Date).AddMonths(-$i).ToString('yyyy-MM-01')
    $existingCount = [string](Get-MySqlScalar "SELECT COUNT(*) FROM monthly_closing_run WHERE branch_id = 1 AND YEAR(closing_month) = YEAR('$candidate') AND MONTH(closing_month) = MONTH('$candidate');")
    if ([string]::IsNullOrWhiteSpace($existingCount) -or [int]$existingCount -eq 0) {
        $MonthlyClosingMonth = $candidate
        break
    }
}
if (-not $MonthlyClosingMonth) {
    $MonthlyClosingMonth = (Get-Date).AddMonths(-18).ToString('yyyy-MM-01')
}

function Add-Result {
    param(
        [string]$Process,
        [string]$Step,
        [string]$Role,
        [string]$Status,
        [string]$Details
    )

    $Results.Add([PSCustomObject]@{
        Process = $Process
        Step = $Step
        Role = $Role
        Status = $Status
        Details = $Details
        Timestamp = (Get-Date).ToString('s')
    })
}

function Parse-JsonSafe {
    param([string]$Text)
    if ([string]::IsNullOrWhiteSpace($Text)) { return $null }
    try { return $Text | ConvertFrom-Json -Depth 20 } catch { return $null }
}

function Invoke-Api {
    param(
        [ValidateSet('GET','POST','PUT','DELETE')]
        [string]$Method,
        [string]$Path,
        [object]$Body = $null,
        [string]$Token = $null,
        [hashtable]$Query = $null,
        [string]$OutFile = $null
    )

    $uriBuilder = [System.UriBuilder]::new("$BaseUrl$Path")
    if ($Query) {
        $pairs = foreach ($key in $Query.Keys) {
            $value = $Query[$key]
            if ($null -ne $value -and "$value" -ne '') {
                '{0}={1}' -f [System.Uri]::EscapeDataString($key), [System.Uri]::EscapeDataString([string]$value)
            }
        }
        $uriBuilder.Query = ($pairs -join '&')
    }

    $headers = @{}
    if ($Token) {
        $headers['Authorization'] = "Bearer $Token"
    }

    $params = @{
        Uri = $uriBuilder.Uri.AbsoluteUri
        Method = $Method
        Headers = $headers
        UseBasicParsing = $true
    }

    if ($null -ne $Body) {
        $params['ContentType'] = 'application/json'
        $params['Body'] = ($Body | ConvertTo-Json -Depth 20)
    }

    if ($OutFile) {
        $params['OutFile'] = $OutFile
    }

    try {
        $response = Invoke-WebRequest @params
        $json = $null
        if (-not $OutFile) {
            $json = Parse-JsonSafe $response.Content
        }
        return [PSCustomObject]@{
            Ok = $true
            StatusCode = [int]$response.StatusCode
            Json = $json
            Headers = $response.Headers
            Content = if ($OutFile) { $null } else { $response.Content }
        }
    } catch {
        $httpResponse = $_.Exception.Response
        $statusCode = if ($httpResponse) { [int]$httpResponse.StatusCode } else { 0 }
        $bodyText = $null
        if ($httpResponse) {
            $reader = New-Object System.IO.StreamReader($httpResponse.GetResponseStream())
            $bodyText = $reader.ReadToEnd()
            $reader.Close()
        }
        return [PSCustomObject]@{
            Ok = $false
            StatusCode = $statusCode
            Json = Parse-JsonSafe $bodyText
            Headers = $null
            Content = $bodyText
            ErrorMessage = $_.Exception.Message
        }
    }
}

function Ensure-SeedLogin {
    param(
        [string]$Username,
        [string]$Password
    )

    $forgot = Invoke-RestMethod -Uri "$BaseUrl/auth/forgot-password" -Method Post -ContentType 'application/json' -Body (@{
        identifier = $Username
        channelType = 'EMAIL'
    } | ConvertTo-Json)

    $provider = [string]$forgot.data.providerResponse
    if ($provider -notmatch 'otpPreview=([0-9]{6})') {
        throw "Unable to extract OTP preview for password reset: $Username"
    }
    $resetOtp = $Matches[1]

    $reset = Invoke-RestMethod -Uri "$BaseUrl/auth/reset-password" -Method Post -ContentType 'application/json' -Body (@{
        requestId = $forgot.data.id
        otpCode = $resetOtp
        newPassword = $Password
        confirmPassword = $Password
    } | ConvertTo-Json)

    return Login-User -Username $Username -Password $Password
}

function Login-User {
    param(
        [string]$Username,
        [string]$Password
    )

    $login = Invoke-RestMethod -Uri "$BaseUrl/auth/login" -Method Post -ContentType 'application/json' -Body (@{
        username = $Username
        password = $Password
    } | ConvertTo-Json)

    $verify = Invoke-RestMethod -Uri "$BaseUrl/auth/verify-login-otp" -Method Post -ContentType 'application/json' -Body (@{
        requestId = $login.data.otpRequestId
        otpCode = $login.data.otpPreviewCode
    } | ConvertTo-Json)

    return $verify.data.session
}

function Expect-Ok {
    param(
        [string]$Process,
        [string]$Step,
        [string]$Role,
        [object]$Response
    )
    if ($Response.Ok) {
        Add-Result -Process $Process -Step $Step -Role $Role -Status 'PASS' -Details "HTTP $($Response.StatusCode)"
        return $true
    }
    Add-Result -Process $Process -Step $Step -Role $Role -Status 'FAIL' -Details "HTTP $($Response.StatusCode) :: $($Response.Content)"
    return $false
}

function Expect-Forbidden {
    param(
        [string]$Process,
        [string]$Step,
        [string]$Role,
        [object]$Response
    )
    if (-not $Response.Ok -and $Response.StatusCode -in 401,403) {
        Add-Result -Process $Process -Step $Step -Role $Role -Status 'PASS' -Details "Expected denial HTTP $($Response.StatusCode)"
        return $true
    }
    Add-Result -Process $Process -Step $Step -Role $Role -Status 'FAIL' -Details "Expected 401/403 but got HTTP $($Response.StatusCode)"
    return $false
}

try {
    Invoke-Expression "& 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe' -uroot -proot -D sbms -e `"UPDATE otp_verification_request SET request_status='EXPIRED', updated_at=NOW() WHERE request_status='SENT';`""
} catch {
    Add-Result -Process 'Setup' -Step 'Expire stale OTP requests' -Role 'SYSTEM' -Status 'WARN' -Details $_.Exception.Message
}

$seedUsers = @(
    @{ Username = 'Admin01'; Password = 'Test@1234'; Key = 'admin' },
    @{ Username = 'branch.manager01'; Password = 'Test@1234'; Key = 'branchManager' },
    @{ Username = 'ops.officer01'; Password = 'Test@1234'; Key = 'ops' },
    @{ Username = 'investment.officer01'; Password = 'Test@1234'; Key = 'investment' },
    @{ Username = 'shariah.board01'; Password = 'Test@1234'; Key = 'shariah' },
    @{ Username = 'customer.seed01'; Password = 'Test@1234'; Key = 'customer' }
)

foreach ($seed in $seedUsers) {
    try {
        $session = Ensure-SeedLogin -Username $seed.Username -Password $seed.Password
        $Tokens[$seed.Key] = $session.token
        Add-Result -Process 'Auth' -Step "Login and OTP for $($seed.Username)" -Role $session.roleCode -Status 'PASS' -Details "Permissions=$(@($session.permissions).Count)"
    } catch {
        Add-Result -Process 'Auth' -Step "Login and OTP for $($seed.Username)" -Role 'UNKNOWN' -Status 'FAIL' -Details $_.Exception.Message
    }
}

$adminToken = $Tokens['admin']
$branchManagerToken = $Tokens['branchManager']
$opsToken = $Tokens['ops']
$investmentToken = $Tokens['investment']
$shariahToken = $Tokens['shariah']
$customerToken = $Tokens['customer']

Expect-Ok -Process 'RBAC' -Step 'Admin users list' -Role 'SYSTEM_ADMIN' -Response (Invoke-Api -Method GET -Path '/users/list' -Token $adminToken) | Out-Null
Expect-Forbidden -Process 'RBAC' -Step 'Customer users list denied' -Role 'CUSTOMER' -Response (Invoke-Api -Method GET -Path '/users/list' -Token $customerToken) | Out-Null
Expect-Ok -Process 'RBAC' -Step 'Admin roles list' -Role 'SYSTEM_ADMIN' -Response (Invoke-Api -Method GET -Path '/roles/list' -Token $adminToken) | Out-Null

$stamp = Get-Date -Format 'HHmmss'
$branchStaffUsername = "branch.staff.$stamp"
$tellerUsername = "teller.$stamp"

$branchStaffCreate = Invoke-Api -Method POST -Path '/users/create' -Token $adminToken -Body @{
    username = $branchStaffUsername
    password = 'Test@1234'
    fullName = 'Smoke Branch Staff'
    email = "$branchStaffUsername@sbms.local"
    mobile = "01701$stamp"
    employeeNo = "BS-$stamp"
    designation = 'Branch Staff'
    branchId = 1
    userType = 'STAFF'
    status = 'ACTIVE'
    roleId = 25
    active = $true
    locked = $false
    actionBy = 'Admin01'
}
if (Expect-Ok -Process 'User Provisioning' -Step 'Create branch staff user' -Role 'SYSTEM_ADMIN' -Response $branchStaffCreate) {
    $branchStaffUserId = [string](Get-MySqlScalar "SELECT id FROM users WHERE username = '$branchStaffUsername' ORDER BY id DESC LIMIT 1;")
    if ($branchStaffUserId) {
        $Created['branchStaffUser'] = [PSCustomObject]@{ id = [long]$branchStaffUserId; username = $branchStaffUsername }
    }
}

$tellerCreate = Invoke-Api -Method POST -Path '/users/create' -Token $adminToken -Body @{
    username = $tellerUsername
    password = 'Test@1234'
    fullName = 'Smoke Teller'
    email = "$tellerUsername@sbms.local"
    mobile = "01801$stamp"
    employeeNo = "TL-$stamp"
    designation = 'Teller'
    branchId = 1
    userType = 'STAFF'
    status = 'ACTIVE'
    roleId = 3
    active = $true
    locked = $false
    actionBy = 'Admin01'
}
if (Expect-Ok -Process 'User Provisioning' -Step 'Create teller user' -Role 'SYSTEM_ADMIN' -Response $tellerCreate) {
    $tellerUserId = [string](Get-MySqlScalar "SELECT id FROM users WHERE username = '$tellerUsername' ORDER BY id DESC LIMIT 1;")
    if ($tellerUserId) {
        $Created['tellerUser'] = [PSCustomObject]@{ id = [long]$tellerUserId; username = $tellerUsername }
    }
}

if ($branchStaffCreate.Ok) {
    try {
        $session = Login-User -Username $branchStaffUsername -Password 'Test@1234'
        $Tokens['branchStaff'] = $session.token
        Add-Result -Process 'Auth' -Step 'Branch staff login' -Role $session.roleCode -Status 'PASS' -Details 'New branch staff authenticated'
    } catch {
        Add-Result -Process 'Auth' -Step 'Branch staff login' -Role 'BRANCH_STAFF' -Status 'FAIL' -Details $_.Exception.Message
    }
}

if ($tellerCreate.Ok) {
    try {
        $session = Login-User -Username $tellerUsername -Password 'Test@1234'
        $Tokens['teller'] = $session.token
        Add-Result -Process 'Auth' -Step 'Teller login' -Role $session.roleCode -Status 'PASS' -Details 'New teller authenticated'
    } catch {
        Add-Result -Process 'Auth' -Step 'Teller login' -Role 'TELLER' -Status 'FAIL' -Details $_.Exception.Message
    }
}

$branchStaffToken = $Tokens['branchStaff']
$tellerToken = $Tokens['teller']

$customerCreate = $null
if ($branchStaffToken) {
    $customerCreate = Invoke-Api -Method POST -Path '/customers/create' -Token $branchStaffToken -Body @{
        customerType = 'INDIVIDUAL'
        fullName = "Smoke Customer $stamp"
        fatherName = 'Abdul Karim'
        motherName = 'Rokeya Begum'
        spouseName = ''
        dateOfBirth = '1992-01-15'
        gender = 'MALE'
        maritalStatus = 'SINGLE'
        nationality = 'Bangladeshi'
        mobile = "01901$stamp"
        email = "smoke.customer.$stamp@sbms.local"
        occupation = 'Small Business'
        monthlyIncome = 55000
        sourceOfFunds = 'Business Income'
        branchId = 1
        customerStatus = 'PENDING_KYC'
        status = 'ACTIVE'
    }
    if (Expect-Ok -Process 'Customer Onboarding' -Step 'Create customer' -Role 'BRANCH_STAFF' -Response $customerCreate) {
        $customerId = [string](Get-MySqlScalar "SELECT id FROM customer WHERE email = 'smoke.customer.$stamp@sbms.local' ORDER BY id DESC LIMIT 1;")
        if ($customerId) {
            $Created['customer'] = [PSCustomObject]@{ id = [long]$customerId; email = "smoke.customer.$stamp@sbms.local" }
        }
    }
}

$kycCreate = $null
if ($Created['customer']) {
    $kycCreate = Invoke-Api -Method POST -Path '/kyc/profile/create' -Token $branchStaffToken -Body @{
        customerId = $Created['customer'].id
        riskLevel = 'LOW'
        sourceOfFundsNote = 'Smoke test customer declared business income'
        pepFlag = $false
        sanctionFlag = $false
        amlFlag = $false
        reviewStatus = 'DRAFT'
        remarks = 'Smoke test KYC profile'
        status = 'ACTIVE'
    }
    if (Expect-Ok -Process 'KYC' -Step 'Create KYC profile' -Role 'BRANCH_STAFF' -Response $kycCreate) {
        $kycId = [string](Get-MySqlScalar "SELECT id FROM kyc_profile WHERE customer_id = $($Created['customer'].id) ORDER BY id DESC LIMIT 1;")
        if ($kycId) {
            $Created['kyc'] = [PSCustomObject]@{ id = [long]$kycId }
        }
    }
}

if ($Created['kyc']) {
    Expect-Ok -Process 'KYC' -Step 'Upload KYC document' -Role 'BRANCH_STAFF' -Response (Invoke-Api -Method POST -Path '/kyc/document/upload' -Token $branchStaffToken -Body @{
        customerId = $Created['customer'].id
        documentType = 'NID'
        fileReferenceId = "SMOKE-KYC-$stamp"
        documentNo = "19999$stamp"
        issueDate = '2020-01-01'
        expiryDate = '2030-01-01'
        verifiedFlag = $false
        status = 'ACTIVE'
    }) | Out-Null
    Expect-Ok -Process 'KYC' -Step 'Submit KYC profile' -Role 'BRANCH_STAFF' -Response (Invoke-Api -Method POST -Path "/kyc/profile/$($Created['kyc'].id)/submit" -Token $branchStaffToken) | Out-Null
    Expect-Ok -Process 'KYC' -Step 'Verify KYC profile' -Role 'OPERATIONS_OFFICER' -Response (Invoke-Api -Method POST -Path "/kyc/profile/$($Created['kyc'].id)/verify" -Token $opsToken) | Out-Null
    Expect-Ok -Process 'KYC' -Step 'Approve KYC profile' -Role 'OPERATIONS_OFFICER' -Response (Invoke-Api -Method POST -Path "/kyc/profile/$($Created['kyc'].id)/approve" -Token $opsToken) | Out-Null
}

$accountOpeningCreate = $null
if ($Created['customer']) {
    $accountOpeningCreate = Invoke-Api -Method POST -Path '/account-opening-requests/create' -Token $branchStaffToken -Body @{
        customerId = $Created['customer'].id
        accountTypeId = 15
        branchId = 1
        requestedDate = (Get-Date -Format 'yyyy-MM-dd')
        initialDepositAmount = 1000
        requestStatus = 'DRAFT'
        remarks = 'Smoke account opening request'
        status = 'ACTIVE'
    }
    if (Expect-Ok -Process 'Account Opening' -Step 'Create account opening request' -Role 'BRANCH_STAFF' -Response $accountOpeningCreate) {
        $aorId = [string](Get-MySqlScalar "SELECT id FROM account_opening_request WHERE customer_id = $($Created['customer'].id) ORDER BY id DESC LIMIT 1;")
        if ($aorId) {
            $Created['accountOpening'] = [PSCustomObject]@{ id = [long]$aorId }
        }
    }
}

if ($Created['accountOpening']) {
    Expect-Ok -Process 'Account Opening' -Step 'Submit account opening request' -Role 'BRANCH_STAFF' -Response (Invoke-Api -Method POST -Path "/account-opening-requests/$($Created['accountOpening'].id)/submit" -Token $branchStaffToken) | Out-Null
    Expect-Ok -Process 'Account Opening' -Step 'Verify account opening request' -Role 'OPERATIONS_OFFICER' -Response (Invoke-Api -Method POST -Path "/account-opening-requests/$($Created['accountOpening'].id)/verify" -Token $opsToken) | Out-Null
    Expect-Ok -Process 'Account Opening' -Step 'Approve account opening request' -Role 'BRANCH_MANAGER' -Response (Invoke-Api -Method POST -Path "/account-opening-requests/$($Created['accountOpening'].id)/approve" -Token $branchManagerToken) | Out-Null

    $accountId = [string](Get-MySqlScalar "SELECT id FROM account WHERE customer_id = $($Created['customer'].id) ORDER BY id DESC LIMIT 1;")
    if ($accountId) {
        $Created['account'] = [PSCustomObject]@{ id = [long]$accountId }
        Add-Result -Process 'Account Opening' -Step 'Locate approved account' -Role 'OPERATIONS_OFFICER' -Status 'PASS' -Details "AccountId=$accountId"
        Expect-Ok -Process 'Account Opening' -Step 'Activate approved account' -Role 'OPERATIONS_OFFICER' -Response (Invoke-Api -Method POST -Path "/accounts/$accountId/activate" -Token $opsToken -Body @{ remarks = 'Smoke account activation' }) | Out-Null
    } else {
        Add-Result -Process 'Account Opening' -Step 'Locate approved account' -Role 'OPERATIONS_OFFICER' -Status 'FAIL' -Details 'Approved request did not create account record'
    }

    $aorPreview = Invoke-Api -Method GET -Path "/account-opening-requests/$($Created['accountOpening'].id)/document/preview" -Token $opsToken -OutFile (Join-Path $ArtifactRoot 'account-opening-form.pdf')
    if ($aorPreview.Ok) {
        Add-Result -Process 'Account Opening' -Step 'Preview account opening document' -Role 'OPERATIONS_OFFICER' -Status 'PASS' -Details 'Saved account-opening-form.pdf'
    } else {
        Add-Result -Process 'Account Opening' -Step 'Preview account opening document' -Role 'OPERATIONS_OFFICER' -Status 'FAIL' -Details $aorPreview.Content
    }
}

$depositResponse = $null
if ($Created['account'] -and $tellerToken) {
    $depositResponse = Invoke-Api -Method POST -Path '/transactions/deposit' -Token $tellerToken -Body @{
        branchId = 1
        terminalId = $null
        creditAccountId = $Created['account'].id
        tellerUserId = $Created['tellerUser'].id
        amount = 500
        narration = 'Smoke test opening deposit'
        transactionDate = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
    }
    if (Expect-Ok -Process 'Transactions' -Step 'Post deposit' -Role 'TELLER' -Response $depositResponse) {
        $transactionId = [string](Get-MySqlScalar "SELECT id FROM transaction_journal WHERE credit_account_id = $($Created['account'].id) ORDER BY id DESC LIMIT 1;")
        if ($transactionId) {
            $Created['transaction'] = [PSCustomObject]@{ id = [long]$transactionId }
        }
    }
}

if ($Created['transaction']) {
    $voucher = Invoke-Api -Method GET -Path "/transactions/$($Created['transaction'].id)/voucher/preview" -Token $opsToken -OutFile (Join-Path $ArtifactRoot 'transaction-voucher.pdf')
    if ($voucher.Ok) {
        Add-Result -Process 'Transactions' -Step 'Preview voucher PDF' -Role 'OPERATIONS_OFFICER' -Status 'PASS' -Details 'Saved transaction-voucher.pdf'
    } else {
        Add-Result -Process 'Transactions' -Step 'Preview voucher PDF' -Role 'OPERATIONS_OFFICER' -Status 'FAIL' -Details $voucher.Content
    }

    Expect-Ok -Process 'Transactions' -Step 'Reverse transaction' -Role 'OPERATIONS_OFFICER' -Response (Invoke-Api -Method POST -Path "/transactions/$($Created['transaction'].id)/reverse" -Token $opsToken -Body @{ reason = 'Smoke reversal validation' }) | Out-Null
}

if ($Created['customer'] -and $Created['account']) {
    $stmtCreate = Invoke-Api -Method POST -Path '/customer-statements/request' -Token $opsToken -Body @{
        customerId = $Created['customer'].id
        accountId = $Created['account'].id
        dateFrom = (Get-Date).AddDays(-7).ToString('yyyy-MM-dd')
        dateTo = (Get-Date).ToString('yyyy-MM-dd')
        requestedBy = 'ops.officer01'
    }
    if (Expect-Ok -Process 'Statements' -Step 'Create customer statement request' -Role 'OPERATIONS_OFFICER' -Response $stmtCreate) {
        $statementId = [string](Get-MySqlScalar "SELECT id FROM customer_statement_request WHERE customer_id = $($Created['customer'].id) ORDER BY id DESC LIMIT 1;")
        if ($statementId) {
            $Created['customerStatement'] = [PSCustomObject]@{ id = [long]$statementId }
            $stmtPreview = Invoke-Api -Method GET -Path "/customer-statements/$statementId/preview" -Token $opsToken -OutFile (Join-Path $ArtifactRoot 'customer-statement.pdf')
            if ($stmtPreview.Ok) {
                Add-Result -Process 'Statements' -Step 'Preview customer statement' -Role 'OPERATIONS_OFFICER' -Status 'PASS' -Details 'Saved customer-statement.pdf'
            } else {
                Add-Result -Process 'Statements' -Step 'Preview customer statement' -Role 'OPERATIONS_OFFICER' -Status 'FAIL' -Details $stmtPreview.Content
            }
        }
    }
}

$reportBefore = Invoke-Api -Method GET -Path '/reports/export-history' -Token $adminToken
$kpiReport = Invoke-Api -Method GET -Path '/reports/kpi' -Token $adminToken -Query @{
    dateFrom = (Get-Date).AddMonths(-1).ToString('yyyy-MM-dd')
    dateTo = (Get-Date).ToString('yyyy-MM-dd')
    exportType = 'PDF'
}
Expect-Ok -Process 'Reporting' -Step 'Generate KPI report' -Role 'SYSTEM_ADMIN' -Response $kpiReport | Out-Null
$reportAfter = Invoke-Api -Method GET -Path '/reports/export-history' -Token $adminToken
if ($reportBefore.Ok -and $reportAfter.Ok) {
    Add-Result -Process 'Reporting' -Step 'Export history increment check' -Role 'SYSTEM_ADMIN' -Status 'PASS' -Details "Before=$(@($reportBefore.Json.data).Count), After=$(@($reportAfter.Json.data).Count)"
}
if ($reportAfter.Ok -and @($reportAfter.Json.data).Count -gt 0) {
    $latestExportId = [string](Get-MySqlScalar "SELECT id FROM report_request_log ORDER BY id DESC LIMIT 1;")
    if ($latestExportId) {
        $Created['latestReportLog'] = $latestExportId
        $reportPreview = Invoke-Api -Method GET -Path "/reports/export-history/$latestExportId/preview" -Token $adminToken -OutFile (Join-Path $ArtifactRoot 'latest-report-export')
        if ($reportPreview.Ok) {
            Add-Result -Process 'Reporting' -Step 'Preview exported report file' -Role 'SYSTEM_ADMIN' -Status 'PASS' -Details "ExportLogId=$latestExportId"
        } else {
            Add-Result -Process 'Reporting' -Step 'Preview exported report file' -Role 'SYSTEM_ADMIN' -Status 'FAIL' -Details $reportPreview.Content
        }
    } else {
        Add-Result -Process 'Reporting' -Step 'Preview exported report file' -Role 'SYSTEM_ADMIN' -Status 'FAIL' -Details 'Latest export history entry had no id'
    }
}

$closingCreate = Invoke-Api -Method POST -Path '/monthly-closing-runs/create' -Token $branchManagerToken -Body @{
    branchId = 1
    closingMonth = $MonthlyClosingMonth
    vaultClosedConfirmed = $true
    profitPostedConfirmed = $true
    reversalsReviewed = $true
    statementsGenerated = $true
    remarks = 'Smoke monthly closing run'
}
if (Expect-Ok -Process 'Monthly Closing' -Step 'Create or refresh run' -Role 'BRANCH_MANAGER' -Response $closingCreate) {
    $monthlyClosingId = [string](Get-MySqlScalar "SELECT id FROM monthly_closing_run WHERE branch_id = 1 AND YEAR(closing_month) = YEAR('$MonthlyClosingMonth') AND MONTH(closing_month) = MONTH('$MonthlyClosingMonth') ORDER BY id DESC LIMIT 1;")
    if ($monthlyClosingId) {
        $Created['monthlyClosing'] = $monthlyClosingId
        Expect-Ok -Process 'Monthly Closing' -Step 'Submit run' -Role 'BRANCH_MANAGER' -Response (Invoke-Api -Method POST -Path "/monthly-closing-runs/$monthlyClosingId/submit" -Token $branchManagerToken) | Out-Null
        Expect-Ok -Process 'Monthly Closing' -Step 'Approve run' -Role 'SYSTEM_ADMIN' -Response (Invoke-Api -Method POST -Path "/monthly-closing-runs/$monthlyClosingId/approve" -Token $adminToken -Body @{ remarks = 'Smoke approval' }) | Out-Null
    } else {
        Add-Result -Process 'Monthly Closing' -Step 'Locate created run' -Role 'BRANCH_MANAGER' -Status 'FAIL' -Details 'Monthly closing run not found after create'
    }
}

$financingList = Invoke-Api -Method GET -Path '/financing-applications/list' -Token $investmentToken
if (Expect-Ok -Process 'Financing' -Step 'List financing applications' -Role 'INVESTMENT_OFFICER' -Response $financingList) {
    $latestFinancing = @($financingList.Json.data | Select-Object -First 1)[0]
    if ($latestFinancing) {
        $Created['financing'] = $latestFinancing
        $sanction = Invoke-Api -Method GET -Path "/financing-applications/$($latestFinancing.id)/sanction-letter/preview" -Token $investmentToken -OutFile (Join-Path $ArtifactRoot 'financing-sanction-letter.pdf')
        if ($sanction.Ok) {
            Add-Result -Process 'Financing' -Step 'Preview sanction letter' -Role 'INVESTMENT_OFFICER' -Status 'PASS' -Details 'Saved financing-sanction-letter.pdf'
        } else {
            Add-Result -Process 'Financing' -Step 'Preview sanction letter' -Role 'INVESTMENT_OFFICER' -Status 'FAIL' -Details $sanction.Content
        }
    }
}

$contractList = Invoke-Api -Method GET -Path '/contracts/list' -Token $investmentToken
if (Expect-Ok -Process 'Contracts' -Step 'List contracts' -Role 'INVESTMENT_OFFICER' -Response $contractList) {
    $contract = @($contractList.Json.data | Select-Object -First 1)[0]
    if ($contract) {
        $Created['contract'] = $contract
        $contractPreview = Invoke-Api -Method GET -Path "/contracts/$($contract.id)/print-copy/preview" -Token $investmentToken -OutFile (Join-Path $ArtifactRoot 'contract-print-copy.pdf')
        if ($contractPreview.Ok) {
            Add-Result -Process 'Contracts' -Step 'Preview contract print copy' -Role 'INVESTMENT_OFFICER' -Status 'PASS' -Details 'Saved contract-print-copy.pdf'
        } else {
            Add-Result -Process 'Contracts' -Step 'Preview contract print copy' -Role 'INVESTMENT_OFFICER' -Status 'FAIL' -Details $contractPreview.Content
        }
    }
}

$shariahList = Invoke-Api -Method GET -Path '/shariah-reviews/list' -Token $shariahToken
if (Expect-Ok -Process 'Shariah' -Step 'List shariah cases' -Role 'SHARIAH_BOARD_MEMBER' -Response $shariahList) {
    $case = @($shariahList.Json.data | Where-Object { $_.reviewStatus -eq 'PENDING_REVIEW' } | Select-Object -First 1)[0]
    if ($case) {
        $caseApprove = Invoke-Api -Method POST -Path "/shariah-reviews/$($case.id)/approve" -Token $shariahToken -Body @{
            decisionBy = 'shariah.board01'
            remarks = 'Smoke test shariah approval'
            checklistItems = @()
        }
        Expect-Ok -Process 'Shariah' -Step 'Approve shariah case' -Role 'SHARIAH_BOARD_MEMBER' -Response $caseApprove | Out-Null
    }
}

if ($Created['contract']) {
    $contractSign = Invoke-Api -Method POST -Path "/contracts/$($Created['contract'].id)/shariah-sign" -Token $shariahToken -Body @{
        signedBy = 'shariah.board01'
        remarks = 'Smoke shariah sign'
    }
    if ($contractSign.Ok) {
        Add-Result -Process 'Contracts' -Step 'Shariah sign contract' -Role 'SHARIAH_BOARD_MEMBER' -Status 'PASS' -Details "ContractId=$($Created['contract'].id)"
    } else {
        Add-Result -Process 'Contracts' -Step 'Shariah sign contract' -Role 'SHARIAH_BOARD_MEMBER' -Status 'WARN' -Details "Could not sign contract: $($contractSign.Content)"
    }
}

$zakatProfiles = Invoke-Api -Method GET -Path '/zakat/profile/list' -Token $opsToken
if (Expect-Ok -Process 'Zakat' -Step 'List zakat profiles' -Role 'OPERATIONS_OFFICER' -Response $zakatProfiles) {
    $profile = @($zakatProfiles.Json.data | Select-Object -First 1)[0]
    if ($profile) {
        $sheet = Invoke-Api -Method GET -Path "/zakat/profile/$($profile.id)/sheet/preview" -Token $opsToken -OutFile (Join-Path $ArtifactRoot 'zakat-profile-sheet.pdf')
        if ($sheet.Ok) {
            Add-Result -Process 'Zakat' -Step 'Preview zakat profile sheet' -Role 'OPERATIONS_OFFICER' -Status 'PASS' -Details 'Saved zakat-profile-sheet.pdf'
        } else {
            Add-Result -Process 'Zakat' -Step 'Preview zakat profile sheet' -Role 'OPERATIONS_OFFICER' -Status 'FAIL' -Details $sheet.Content
        }
    }
}

$payouts = Invoke-Api -Method GET -Path '/zakat/payouts/list' -Token $opsToken
if (Expect-Ok -Process 'Zakat' -Step 'List charity payouts' -Role 'OPERATIONS_OFFICER' -Response $payouts) {
    $payout = @($payouts.Json.data | Select-Object -First 1)[0]
    if ($payout) {
        $receipt = Invoke-Api -Method GET -Path "/zakat/payouts/$($payout.id)/receipt/preview" -Token $opsToken -OutFile (Join-Path $ArtifactRoot 'charity-payout-receipt.pdf')
        if ($receipt.Ok) {
            Add-Result -Process 'Zakat' -Step 'Preview payout receipt' -Role 'OPERATIONS_OFFICER' -Status 'PASS' -Details 'Saved charity-payout-receipt.pdf'
        } else {
            Add-Result -Process 'Zakat' -Step 'Preview payout receipt' -Role 'OPERATIONS_OFFICER' -Status 'FAIL' -Details $receipt.Content
        }
    }
}

$terminals = Invoke-Api -Method GET -Path '/atm-terminals/list' -Token $opsToken
if (Expect-Ok -Process 'ATM' -Step 'List ATM terminals' -Role 'OPERATIONS_OFFICER' -Response $terminals) {
    $terminal = @($terminals.Json.data | Select-Object -First 1)[0]
    if ($terminal) {
        $terminalProfile = Invoke-Api -Method GET -Path "/atm-terminals/$($terminal.id)/profile/preview" -Token $opsToken -OutFile (Join-Path $ArtifactRoot 'atm-terminal-profile.pdf')
        if ($terminalProfile.Ok) {
            Add-Result -Process 'ATM' -Step 'Preview ATM terminal profile' -Role 'OPERATIONS_OFFICER' -Status 'PASS' -Details 'Saved atm-terminal-profile.pdf'
        } else {
            Add-Result -Process 'ATM' -Step 'Preview ATM terminal profile' -Role 'OPERATIONS_OFFICER' -Status 'FAIL' -Details $terminalProfile.Content
        }
    }
}

$cashBins = Invoke-Api -Method GET -Path '/atm-terminals/cash-bin/list' -Token $opsToken
if (Expect-Ok -Process 'ATM' -Step 'List cash bins' -Role 'OPERATIONS_OFFICER' -Response $cashBins) {
    $cashBin = @($cashBins.Json.data | Select-Object -First 1)[0]
    if ($cashBin) {
        $cashBinProfile = Invoke-Api -Method GET -Path "/atm-terminals/cash-bin/$($cashBin.id)/profile/preview" -Token $opsToken -OutFile (Join-Path $ArtifactRoot 'atm-cash-bin-profile.pdf')
        if ($cashBinProfile.Ok) {
            Add-Result -Process 'ATM' -Step 'Preview cash bin profile' -Role 'OPERATIONS_OFFICER' -Status 'PASS' -Details 'Saved atm-cash-bin-profile.pdf'
        } else {
            Add-Result -Process 'ATM' -Step 'Preview cash bin profile' -Role 'OPERATIONS_OFFICER' -Status 'FAIL' -Details $cashBinProfile.Content
        }
    }
}

$replenishments = Invoke-Api -Method GET -Path '/atm-terminals/replenishment/list' -Token $opsToken
if (Expect-Ok -Process 'ATM' -Step 'List replenishments' -Role 'OPERATIONS_OFFICER' -Response $replenishments) {
    $replenishment = @($replenishments.Json.data | Select-Object -First 1)[0]
    if ($replenishment) {
        $replenishmentReport = Invoke-Api -Method GET -Path "/atm-terminals/replenishment/$($replenishment.id)/report/preview" -Token $opsToken -OutFile (Join-Path $ArtifactRoot 'atm-replenishment-report.pdf')
        if ($replenishmentReport.Ok) {
            Add-Result -Process 'ATM' -Step 'Preview replenishment report' -Role 'OPERATIONS_OFFICER' -Status 'PASS' -Details 'Saved atm-replenishment-report.pdf'
        } else {
            Add-Result -Process 'ATM' -Step 'Preview replenishment report' -Role 'OPERATIONS_OFFICER' -Status 'FAIL' -Details $replenishmentReport.Content
        }
    }
}

$reconciliations = Invoke-Api -Method GET -Path '/atm-terminals/reconciliation/list' -Token $opsToken
if (Expect-Ok -Process 'ATM' -Step 'List reconciliations' -Role 'OPERATIONS_OFFICER' -Response $reconciliations) {
    $reconciliation = @($reconciliations.Json.data | Select-Object -First 1)[0]
    if ($reconciliation) {
        $reconciliationReport = Invoke-Api -Method GET -Path "/atm-terminals/reconciliation/$($reconciliation.id)/report/preview" -Token $opsToken -OutFile (Join-Path $ArtifactRoot 'atm-reconciliation-report.pdf')
        if ($reconciliationReport.Ok) {
            Add-Result -Process 'ATM' -Step 'Preview reconciliation report' -Role 'OPERATIONS_OFFICER' -Status 'PASS' -Details 'Saved atm-reconciliation-report.pdf'
        } else {
            Add-Result -Process 'ATM' -Step 'Preview reconciliation report' -Role 'OPERATIONS_OFFICER' -Status 'FAIL' -Details $reconciliationReport.Content
        }
    }
}

$cashLedger = Invoke-Api -Method GET -Path '/branches/cash-ledger' -Token $branchManagerToken -Query @{ branchId = 1 }
if (Expect-Ok -Process 'Branch Operations' -Step 'List cash ledger' -Role 'BRANCH_MANAGER' -Response $cashLedger) {
    $cashLedgerReport = Invoke-Api -Method GET -Path '/branches/cash-ledger/report/preview' -Token $branchManagerToken -Query @{ branchId = 1 } -OutFile (Join-Path $ArtifactRoot 'branch-daily-cash-report.pdf')
    if ($cashLedgerReport.Ok) {
        Add-Result -Process 'Branch Operations' -Step 'Preview daily cash report' -Role 'BRANCH_MANAGER' -Status 'PASS' -Details 'Saved branch-daily-cash-report.pdf'
    } else {
        Add-Result -Process 'Branch Operations' -Step 'Preview daily cash report' -Role 'BRANCH_MANAGER' -Status 'FAIL' -Details $cashLedgerReport.Content
    }
}

$vaults = Invoke-Api -Method GET -Path '/branches/vault/list' -Token $branchManagerToken -Query @{ branchId = 1 }
if (Expect-Ok -Process 'Branch Operations' -Step 'List vault balances' -Role 'BRANCH_MANAGER' -Response $vaults) {
    $vault = @($vaults.Json.data | Select-Object -First 1)[0]
    if ($vault) {
        $vaultReport = Invoke-Api -Method GET -Path "/branches/vault/$($vault.id)/report/preview" -Token $branchManagerToken -OutFile (Join-Path $ArtifactRoot 'vault-balance-report.pdf')
        if ($vaultReport.Ok) {
            Add-Result -Process 'Branch Operations' -Step 'Preview vault balance report' -Role 'BRANCH_MANAGER' -Status 'PASS' -Details 'Saved vault-balance-report.pdf'
        } else {
            Add-Result -Process 'Branch Operations' -Step 'Preview vault balance report' -Role 'BRANCH_MANAGER' -Status 'FAIL' -Details $vaultReport.Content
        }
    }
}

$enrollments = Invoke-Api -Method GET -Path '/deposit-schemes/enrollment/list' -Token $adminToken
if (Expect-Ok -Process 'Deposit Schemes' -Step 'List enrollments' -Role 'SYSTEM_ADMIN' -Response $enrollments) {
    $enrollment = @($enrollments.Json.data | Select-Object -First 1)[0]
    if ($enrollment) {
        $certificate = Invoke-Api -Method GET -Path "/deposit-schemes/enrollment/$($enrollment.id)/certificate/preview" -Token $adminToken -OutFile (Join-Path $ArtifactRoot 'deposit-investment-certificate.pdf')
        if ($certificate.Ok) {
            Add-Result -Process 'Deposit Schemes' -Step 'Preview investment certificate' -Role 'SYSTEM_ADMIN' -Status 'PASS' -Details 'Saved deposit-investment-certificate.pdf'
        } else {
            Add-Result -Process 'Deposit Schemes' -Step 'Preview investment certificate' -Role 'SYSTEM_ADMIN' -Status 'FAIL' -Details $certificate.Content
        }
    }
}

$profitPostings = Invoke-Api -Method GET -Path '/profit-postings/list' -Token $adminToken
if (Expect-Ok -Process 'Profit' -Step 'List profit postings' -Role 'SYSTEM_ADMIN' -Response $profitPostings) {
    $posting = @($profitPostings.Json.data | Select-Object -First 1)[0]
    if ($posting) {
        $profitAdvice = Invoke-Api -Method GET -Path "/profit-postings/$($posting.id)/advice/preview" -Token $adminToken -OutFile (Join-Path $ArtifactRoot 'profit-posting-advice.pdf')
        if ($profitAdvice.Ok) {
            Add-Result -Process 'Profit' -Step 'Preview profit posting advice' -Role 'SYSTEM_ADMIN' -Status 'PASS' -Details 'Saved profit-posting-advice.pdf'
        } else {
            Add-Result -Process 'Profit' -Step 'Preview profit posting advice' -Role 'SYSTEM_ADMIN' -Status 'FAIL' -Details $profitAdvice.Content
        }
    }
}

Expect-Ok -Process 'Security' -Step 'List workflow pending queue' -Role 'SYSTEM_ADMIN' -Response (Invoke-Api -Method GET -Path '/workflows/pending' -Token $adminToken) | Out-Null
Expect-Ok -Process 'Security' -Step 'Verification dashboard summary' -Role 'SYSTEM_ADMIN' -Response (Invoke-Api -Method GET -Path '/verifications/dashboard-summary' -Token $adminToken) | Out-Null
Expect-Ok -Process 'Security' -Step 'Integration dashboard summary' -Role 'SYSTEM_ADMIN' -Response (Invoke-Api -Method GET -Path '/integrations/dashboard-summary' -Token $adminToken) | Out-Null

$resultPath = Join-Path $ArtifactRoot 'smoke-results.json'
$Results | ConvertTo-Json -Depth 10 | Set-Content -Path $resultPath -Encoding UTF8

$summary = [PSCustomObject]@{
    artifactRoot = $ArtifactRoot
    resultFile = $resultPath
    created = $Created
    passCount = @($Results | Where-Object Status -eq 'PASS').Count
    failCount = @($Results | Where-Object Status -eq 'FAIL').Count
    warnCount = @($Results | Where-Object Status -eq 'WARN').Count
    results = $Results
}

$summary | ConvertTo-Json -Depth 12
