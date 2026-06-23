const fs = require('fs');
const path = require('path');
let jsPDF;

try {
  ({ jsPDF } = require('jspdf'));
} catch (error) {
  ({ jsPDF } = require(path.join(__dirname, '..', 'stable-sbms-frontend', 'node_modules', 'jspdf')));
}

const root = path.resolve(__dirname, '..');
const sourcePath = path.join(__dirname, 'SBMS-System-Verification-Playbook-v2.md');
const outputPath = path.join(__dirname, 'SBMS-System-Verification-Playbook-v2.pdf');
const frontendOutputPath = path.join(root, 'stable-sbms-frontend', 'SBMS-System-Verification-Playbook-v2.pdf');

const raw = fs.readFileSync(sourcePath, 'utf8').replace(/\r/g, '');
const lines = raw.split('\n');

const doc = new jsPDF({ unit: 'pt', format: 'a4' });
const pageWidth = doc.internal.pageSize.getWidth();
const pageHeight = doc.internal.pageSize.getHeight();
const left = 48;
const right = 48;
const top = 64;
const bottom = 52;
const contentWidth = pageWidth - left - right;

let y = top;
let page = 1;

function cleanInline(text) {
  return text
    .replace(/[^\x09\x0A\x0D\x20-\x7E]/g, ' ')
    .replace(/`([^`]+)`/g, '$1')
    .replace(/\*\*([^*]+)\*\*/g, '$1')
    .replace(/\*([^*]+)\*/g, '$1')
    .replace(/^[-*]\s+/, '')
    .trimEnd();
}

function drawHeader() {
  doc.setFillColor(7, 70, 47);
  doc.rect(0, 0, pageWidth, 42, 'F');
  doc.setTextColor(255, 255, 255);
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(16);
  doc.text('SBMS System Verification Playbook v2', left, 27);
  doc.setDrawColor(212, 175, 55);
  doc.setLineWidth(1.2);
  doc.line(left, 40, pageWidth - right, 40);
  doc.setTextColor(30, 41, 59);
}

function drawFooter(pageNo) {
  doc.setDrawColor(210, 220, 215);
  doc.setLineWidth(0.7);
  doc.line(left, pageHeight - 32, pageWidth - right, pageHeight - 32);
  doc.setFont('helvetica', 'normal');
  doc.setFontSize(9);
  doc.setTextColor(90, 104, 96);
  doc.text(`Generated from ${path.relative(root, sourcePath)}`, left, pageHeight - 18);
  doc.text(`Page ${pageNo}`, pageWidth - right - 34, pageHeight - 18);
  doc.setTextColor(30, 41, 59);
}

function ensureSpace(heightNeeded = 18) {
  if (y + heightNeeded > pageHeight - bottom) {
    drawFooter(page);
    doc.addPage();
    page += 1;
    drawHeader();
    y = top;
  }
}

function writeWrapped(text, options = {}) {
  const {
    font = 'helvetica',
    style = 'normal',
    size = 11,
    color = [30, 41, 59],
    indent = 0,
    leading = 15,
    gapAfter = 4
  } = options;

  doc.setFont(font, style);
  doc.setFontSize(size);
  doc.setTextColor(...color);
  const wrapped = doc.splitTextToSize(text, contentWidth - indent);
  ensureSpace(wrapped.length * leading + gapAfter);
  doc.text(wrapped, left + indent, y);
  y += wrapped.length * leading + gapAfter;
}

drawHeader();

lines.forEach((line) => {
  const trimmed = line.trim();

  if (!trimmed) {
    y += 6;
    return;
  }

  if (trimmed.startsWith('# ')) {
    y += 8;
    writeWrapped(cleanInline(trimmed.slice(2)), {
      style: 'bold',
      size: 22,
      color: [17, 74, 52],
      leading: 24,
      gapAfter: 10
    });
    return;
  }

  if (trimmed.startsWith('## ')) {
    y += 6;
    writeWrapped(cleanInline(trimmed.slice(3)), {
      style: 'bold',
      size: 15,
      color: [13, 95, 68],
      leading: 18,
      gapAfter: 8
    });
    return;
  }

  if (trimmed.startsWith('### ')) {
    writeWrapped(cleanInline(trimmed.slice(4)), {
      style: 'bold',
      size: 12,
      color: [117, 86, 17],
      leading: 15,
      gapAfter: 5
    });
    return;
  }

  if (/^\|/.test(trimmed)) {
    writeWrapped(cleanInline(trimmed), {
      size: 9.5,
      leading: 12,
      gapAfter: 2
    });
    return;
  }

  if (/^\d+\.\s+/.test(trimmed)) {
    const text = trimmed.replace(/^(\d+\.)\s+/, '$1 ');
    writeWrapped(cleanInline(text), {
      size: 10.5,
      indent: 10,
      leading: 14,
      gapAfter: 2
    });
    return;
  }

  if (trimmed.startsWith('- ')) {
    writeWrapped(`- ${cleanInline(trimmed.slice(2))}`, {
      size: 10.5,
      indent: 12,
      leading: 14,
      gapAfter: 2
    });
    return;
  }

  writeWrapped(cleanInline(trimmed), {
    size: 10.5,
    leading: 14,
    gapAfter: 3
  });
});

drawFooter(page);

const pdfBuffer = Buffer.from(doc.output('arraybuffer'));
fs.writeFileSync(outputPath, pdfBuffer);
fs.writeFileSync(frontendOutputPath, pdfBuffer);

console.log(`Generated: ${outputPath}`);
console.log(`Copied to: ${frontendOutputPath}`);
