package com.sbms.account.controller.impl;

import com.sbms.account.controller.IAccountTypeController;
import com.sbms.account.dto.request.AccountTypeRequest;
import com.sbms.account.dto.response.AccountTypeDropdownResponse;
import com.sbms.account.dto.response.AccountTypeResponse;
import com.sbms.account.service.IAccountTypeService;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account-types")
@RequiresPermission("ACCOUNT_MANAGEMENT_ACCESS")
public class AccountTypeController implements IAccountTypeController {

    @Autowired
    private IAccountTypeService accountTypeService;

    @Override
    @RequiresPermission("ACCOUNT_TYPE_CREATE")
    @PostMapping("/create")
    public ApiResponse<AccountTypeResponse> create(@RequestBody AccountTypeRequest request) {
        return ResponseBuilder.success("Account type created successfully", accountTypeService.create(request));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<AccountTypeResponse>> list() {
        return ResponseBuilder.success("Account type list fetched successfully", accountTypeService.list());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<AccountTypeResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Account type fetched successfully", accountTypeService.getById(id));
    }

    @Override
    @RequiresPermission("ACCOUNT_TYPE_EDIT")
    @PutMapping("/{id}")
    public ApiResponse<AccountTypeResponse> update(@PathVariable Long id, @RequestBody AccountTypeRequest request) {
        return ResponseBuilder.success("Account type updated successfully", accountTypeService.update(id, request));
    }

    @Override
    @RequiresPermission("ACCOUNT_TYPE_ARCHIVE")
    @DeleteMapping("/{id}")
    public ApiResponse<AccountTypeResponse> archive(@PathVariable Long id) {
        return ResponseBuilder.success("Account type archived successfully", accountTypeService.archive(id));
    }

    @Override
    @RequiresPermission("ACCOUNT_TYPE_RESTORE")
    @PutMapping("/{id}/restore")
    public ApiResponse<AccountTypeResponse> restore(@PathVariable Long id) {
        return ResponseBuilder.success("Account type restored successfully", accountTypeService.restore(id));
    }

    @Override
    @GetMapping("/dropdown")
    public ApiResponse<List<AccountTypeDropdownResponse>> dropdown() {
        return ResponseBuilder.success("Account type dropdown fetched successfully", accountTypeService.dropdown());
    }
}
