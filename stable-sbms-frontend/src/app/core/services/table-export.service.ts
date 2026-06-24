import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

export type TableExportFormat = 'csv' | 'excel' | 'pdf';

@Injectable({
  providedIn: 'root'
})
export class TableExportService {

  printTableDocument(title: string, subtitle: string, headers: string[], rows: string[][]): void {
    const htmlRows = rows.map((row, index) => `
      <tr>
        <td>${index + 1}</td>
        ${row.map(value => `<td>${this.safe(value)}</td>`).join('')}
      </tr>
    `).join('');

    const html = `
      <html>
        <head>
          <title>${this.safe(title)}</title>
          <style>
            body { font-family: Arial, sans-serif; padding: 18px; color: #0f172a; }
            .print-header { display:flex; justify-content:space-between; border-bottom:2px solid #0d9488; padding-bottom:10px; margin-bottom:14px; }
            h2 { margin:0; font-size:20px; }
            p { margin:4px 0 0; color:#64748b; font-size:12px; }
            .meta { text-align:right; font-size:11px; color:#64748b; }
            table { width:100%; border-collapse:collapse; table-layout:fixed; font-size:10.5px; }
            th, td { border:1px solid #cbd5e1; padding:6px 7px; text-align:left; word-wrap:break-word; }
            th { background:#f1f5f9; color:#334155; font-weight:700; }
            @page { size:A4 landscape; margin:8mm; }
          </style>
        </head>
        <body>
          <div class="print-header">
            <div>
              <h2>${this.safe(title)}</h2>
              <p>${this.safe(subtitle)}</p>
            </div>
            <div class="meta">
              Total Records: ${rows.length}<br>
              Printed: ${new Date().toLocaleString()}
            </div>
          </div>
          <table>
            <thead>
              <tr>
                <th>#</th>
                ${headers.map(header => `<th>${this.safe(header)}</th>`).join('')}
              </tr>
            </thead>
            <tbody>${htmlRows}</tbody>
          </table>
        </body>
      </html>
    `;

    const printWindow = window.open('', '_blank');
    if (!printWindow) {
      Swal.fire('Popup blocked', 'Please allow popup and try again.', 'error');
      return;
    }

    printWindow.document.open();
    printWindow.document.write(html);
    printWindow.document.close();
    printWindow.onload = () => {
      printWindow.focus();
      printWindow.print();
    };
  }

  exportTable(title: string, fileBase: string, headers: string[], rows: string[][], format: TableExportFormat): void {
    if (format === 'csv') {
      this.exportCsv(fileBase, headers, rows);
      return;
    }
    if (format === 'excel') {
      this.exportExcel(title, fileBase, headers, rows);
      return;
    }
    this.exportPdf(title, fileBase, headers, rows);
  }

  private exportCsv(fileBase: string, headers: string[], rows: string[][]): void {
    const csv = [headers, ...rows]
      .map(row => row.map(value => `"${String(value ?? '').replace(/"/g, '""')}"`).join(','))
      .join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    this.downloadBlob(blob, `${fileBase}.csv`);
  }

  private exportExcel(title: string, fileBase: string, headers: string[], rows: string[][]): void {
    import('xlsx').then(xlsx => {
      const records = rows.map(row => headers.reduce((acc, header, index) => {
        acc[header] = row[index] || '';
        return acc;
      }, {} as Record<string, string>));

      const worksheet = xlsx.utils.json_to_sheet(records);
      const workbook = {
        Sheets: { [title]: worksheet },
        SheetNames: [title]
      };
      const buffer = xlsx.write(workbook, { bookType: 'xlsx', type: 'array' });
      this.downloadBlob(new Blob([buffer], { type: 'application/octet-stream' }), `${fileBase}.xlsx`);
    });
  }

  private exportPdf(title: string, fileBase: string, headers: string[], rows: string[][]): void {
    import('jspdf').then(jsPDF => {
      import('jspdf-autotable').then(() => {
        const doc = new jsPDF.jsPDF('landscape');
        doc.text(title, 14, 14);
        (doc as any).autoTable({
          head: [['#', ...headers]],
          body: rows.map((row, index) => [String(index + 1), ...row]),
          startY: 20,
          styles: { fontSize: 8 }
        });
        doc.save(`${fileBase}.pdf`);
      });
    });
  }

  private downloadBlob(blob: Blob, fileName: string): void {
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    link.click();
    URL.revokeObjectURL(url);
  }

  private safe(value: string): string {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }
}
