import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { ShariahReviewCaseResponse, formatEnumLabel } from '../../models/shariah.model';
import { ShariahService } from '../../services/shariah.service';

@Component({
  selector: 'app-fatwa-certificate-list',
  templateUrl: './fatwa-certificate-list.component.html',
  styleUrls: ['./fatwa-certificate-list.component.scss']
})
export class FatwaCertificateListComponent implements OnInit {

  loading = false;
  items: ShariahReviewCaseResponse[] = [];

  constructor(
    private shariahService: ShariahService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.shariahService.getCases({ caseStatus: 'APPROVED' }).subscribe({
      next: data => {
        this.items = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load fatwa certificate list.', 'error');
      }
    });
  }

  openView(item: ShariahReviewCaseResponse): void {
    this.router.navigate(['/shariah/cases', item.id]);
  }

  printCertificate(item: ShariahReviewCaseResponse): void {
    const approvedDecision = this.latestApprovedDecision(item);
    const issuedDate = this.formatCertificateDate(approvedDecision?.decisionAt || item.updatedAt || item.createdAt);
    const logoUrl = `${window.location.origin}/assets/branding/sbms-logo-mark.png`;
    const caseNo = this.escapeHtml(item.caseNo);
    const referenceModule = this.escapeHtml(formatEnumLabel(item.referenceModule));
    const referenceId = this.escapeHtml(String(item.referenceId ?? '-'));
    const submittedBy = this.escapeHtml(item.submittedBy || '-');
    const approvedBy = this.escapeHtml(approvedDecision?.decisionBy || item.history?.[0]?.decisionBy || 'Shariah Board');
    const remarks = this.escapeHtml(approvedDecision?.remarks || item.remarks || 'The submitted reference has been reviewed and approved under the applicable Shariah governance checklist.');
    const status = this.escapeHtml(formatEnumLabel(item.caseStatus));

    const html = `
      <html>
        <head>
          <title>Fatwa Certificate - ${item.caseNo}</title>
          <style>
            * { box-sizing: border-box; }
            body {
              margin: 0;
              padding: 22px;
              background: #f2f5f3;
              font-family: Arial, Helvetica, sans-serif;
              color: #0f172a;
            }
            .certificate-shell {
              max-width: 920px;
              margin: 0 auto;
              background: #ffffff;
              border: 10px solid #0b5c44;
              outline: 2px solid #d4af37;
              outline-offset: -18px;
              padding: 42px 52px 36px;
              min-height: 940px;
              position: relative;
              box-shadow: 0 20px 52px rgba(15, 23, 42, 0.12);
            }
            .certificate-shell::before {
              content: "";
              position: absolute;
              inset: 24px;
              border: 1px solid rgba(212, 175, 55, 0.58);
              pointer-events: none;
            }
            .brand {
              text-align: center;
              position: relative;
              z-index: 1;
            }
            .logo {
              width: 78px;
              height: 78px;
              border-radius: 18px;
              border: 1px solid #cfe5df;
              background: #f7fbfa;
              object-fit: contain;
              padding: 8px;
              margin-bottom: 10px;
            }
            .bank-name { font-size: 27px; font-weight: 800; line-height: 1.12; color: #0f2d23; }
            .bank-sub { margin-top: 4px; font-size: 12px; color: #5f6f67; }
            .kicker {
              margin-top: 34px;
              font-size: 12px;
              font-weight: 800;
              letter-spacing: 2.4px;
              text-transform: uppercase;
              color: #0b694d;
            }
            h1 {
              margin: 10px 0 0;
              font-family: Georgia, 'Times New Roman', serif;
              font-size: 44px;
              line-height: 1.08;
              color: #14251d;
              font-weight: 700;
            }
            .title-rule {
              width: 170px;
              height: 3px;
              margin: 18px auto 0;
              background: linear-gradient(90deg, transparent, #d4af37, transparent);
            }
            .cert-meta {
              margin: 30px auto 0;
              width: 100%;
              max-width: 680px;
              display: flex;
              justify-content: space-between;
              gap: 18px;
              font-size: 13px;
              color: #334155;
            }
            .cert-meta span { color: #64756d; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; font-size: 10px; display: block; margin-bottom: 5px; }
            .cert-meta strong { color: #102a20; }
            .statement {
              margin: 42px auto 0;
              max-width: 760px;
              color: #334155;
              font-family: Georgia, 'Times New Roman', serif;
              font-size: 18px;
              line-height: 1.9;
              text-align: center;
            }
            .statement strong { color: #17452e; }
            .approval-seal {
              width: 128px;
              height: 128px;
              border: 2px solid #0b694d;
              border-radius: 999px;
              margin: 34px auto 0;
              display: flex;
              align-items: center;
              justify-content: center;
              text-align: center;
              color: #0b694d;
              font-size: 12px;
              font-weight: 800;
              letter-spacing: 1.4px;
              text-transform: uppercase;
              box-shadow: inset 0 0 0 7px #f4fbf8;
            }
            .signature-strip {
              display: grid;
              grid-template-columns: repeat(3, minmax(0, 1fr));
              gap: 28px;
              margin-top: 72px;
            }
            .signature-box {
              border-top: 1px solid #8ea59a;
              padding: 14px 8px 0;
              text-align: center;
            }
            .signature-box span { display: block; font-size: 10px; font-weight: 800; letter-spacing: 1.6px; text-transform: uppercase; color: #6d7c74; margin-bottom: 8px; }
            .signature-box strong { font-size: 13px; color: #173728; }
            .footer {
              margin-top: 42px;
              padding-top: 14px;
              border-top: 1px solid #e1ebe6;
              text-align: center;
              color: #6b7d73;
              font-size: 11px;
            }
            @page { size: A4 portrait; margin: 10mm; }
            @media print {
              body { background: #fff; padding: 0; }
              .certificate-shell { box-shadow: none; }
              .certificate-shell, .approval-seal { -webkit-print-color-adjust: exact; print-color-adjust: exact; }
            }
          </style>
        </head>
        <body>
          <div class="certificate-shell">
            <div class="brand">
              <img class="logo" src="${logoUrl}" alt="Al-Barakah Shariah Bank PLC logo">
              <div class="bank-name">Al-Barakah Shariah Bank PLC</div>
              <div class="bank-sub">Islamic Banking Document &amp; Report Center</div>
              <div class="bank-sub">Shariah Supervisory Board Secretariat</div>
              <div class="kicker">Fatwa Certificate</div>
              <h1>Certificate of Shariah Approval</h1>
              <div class="title-rule"></div>
              <div class="cert-meta">
                <div><span>Certificate No</span><strong>${caseNo}</strong></div>
                <div><span>Issue Date</span><strong>${issuedDate}</strong></div>
                <div><span>Status</span><strong>${status}</strong></div>
              </div>
            </div>
            <div class="statement">
              This is to certify that the referenced case <strong>${referenceModule} / ${referenceId}</strong>
              submitted by <strong>${submittedBy}</strong> has been reviewed by the Shariah governance process
              and has been approved for compliance record purposes.
              <br><br>
              <strong>Approval note:</strong> ${remarks}
            </div>
            <div class="approval-seal">Shariah<br>Approved</div>
            <div class="signature-strip">
              <div class="signature-box"><span>Prepared By</span><strong>Shariah Secretariat</strong></div>
              <div class="signature-box"><span>Reviewed By</span><strong>${approvedBy}</strong></div>
              <div class="signature-box"><span>Authorized Copy</span><strong>System Generated</strong></div>
            </div>
            <div class="footer">
              This certificate is system generated for audit, board record and operational compliance reference.
            </div>
          </div>
        </body>
      </html>
    `;
    const win = window.open('', '_blank');
    if (!win) {
      Swal.fire('Popup blocked', 'Please allow popup and try again.', 'error');
      return;
    }
    win.document.open();
    win.document.write(html);
    win.document.close();
    win.onload = () => {
      win.focus();
      win.print();
    };
  }

  print(): void {
    window.print();
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  private latestApprovedDecision(item: ShariahReviewCaseResponse): any {
    return [...(item.history || [])]
      .reverse()
      .find(decision => decision.decision === 'APPROVED') || null;
  }

  private formatCertificateDate(value?: string | null): string {
    if (!value) {
      return new Date().toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
    }
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }
    return date.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  private escapeHtml(value: string): string {
    return String(value || '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }
}
