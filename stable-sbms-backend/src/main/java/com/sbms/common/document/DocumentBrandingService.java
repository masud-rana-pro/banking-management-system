package com.sbms.common.document;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class DocumentBrandingService {

    private static final String BANK_NAME = "Al-Barakah Shariah Bank PLC";
    private static final String BANK_TAGLINE = "Islamic Banking Document & Report Center";
    private static final String BANK_ADDRESS = "Head Office, Motijheel Commercial Area, Dhaka, Bangladesh";
    private static final String BANK_CONTACT = "+880 1234 567890 | info@albarakahbank.com | www.albarakahbank.com";
    private static final String FRONTEND_LOGO_PATH = "stable-sbms-frontend/src/assets/branding/sbms-logo-mark.png";

    public String getBankName() {
        return BANK_NAME;
    }

    public String getBankTagline() {
        return BANK_TAGLINE;
    }

    public String getBankAddress() {
        return BANK_ADDRESS;
    }

    public String getBankContact() {
        return BANK_CONTACT;
    }

    public String getLogoDataUri() {
        try {
            Path logoPath = getLogoPath();
            if (!Files.exists(logoPath)) {
                return "";
            }
            byte[] bytes = Files.readAllBytes(logoPath);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException ex) {
            return "";
        }
    }

    public Path getLogoPath() {
        return resolveWorkspaceRoot().resolve(FRONTEND_LOGO_PATH).normalize();
    }

    public String getPremiumDocumentStyle(String accentCss) {
        return "body{margin:0;background:#edf5f4;font-family:Arial,'Helvetica Neue',sans-serif;color:#0f172a;padding:18px;}" +
                ".doc-shell{max-width:1120px;margin:0 auto;background:#fff;border:1px solid #cbdedb;box-shadow:0 14px 34px rgba(15,23,42,.10);}" +
                ".doc-top-band{display:flex;align-items:center;gap:12px;padding:8px 24px 6px;background:#f8fffd;color:#0f172a;border-bottom:1px solid #d7e7e4;}" +
                ".doc-top-line{height:1px;flex:1;background:#0f766e;opacity:.7;}" +
                ".doc-top-title{font-size:10px;font-weight:800;letter-spacing:.18em;text-transform:uppercase;white-space:nowrap;color:#0f766e;}" +
                ".doc-accent{height:8px;background:linear-gradient(90deg,#064e3b 0 38%,#0d9488 38% 72%,#f59e0b 72% 100%);}" +
                accentCss +
                ".doc-header{display:flex;justify-content:space-between;gap:22px;padding:22px 28px 18px;border-bottom:2px solid #0f766e;}" +
                ".doc-brand-row{display:flex;align-items:center;gap:14px;min-width:0;}" +
                ".doc-logo{width:74px;height:58px;object-fit:contain;background:#fff;border:1px solid #cce3df;padding:6px;}" +
                ".doc-bank-meta{display:flex;flex-direction:column;gap:3px;min-width:0;}" +
                ".doc-brand{font-size:29px;font-weight:900;line-height:1;color:#064e3b;letter-spacing:.01em;}" +
                ".doc-bank-sub{font-size:11px;color:#475569;line-height:1.35;}" +
                ".doc-chip-panel{min-width:230px;border-left:4px solid #f59e0b;padding:2px 0 2px 14px;display:flex;flex-direction:column;gap:5px;align-self:flex-start;}" +
                ".doc-chip-label{font-size:10px;font-weight:800;letter-spacing:.14em;text-transform:uppercase;color:#0f766e;}" +
                ".doc-chip{display:inline-flex;align-self:flex-start;padding:7px 16px;border-radius:6px;background:#0f766e;font-size:13px;font-weight:900;color:#fff;text-transform:uppercase;letter-spacing:.05em;}" +
                ".doc-chip-meta{font-size:12px;color:#0f172a;font-weight:700;}" +
                ".doc-title-row{display:flex;justify-content:space-between;gap:22px;padding:20px 28px 0;}" +
                ".doc-section-kicker{font-size:10px;font-weight:900;letter-spacing:.15em;text-transform:uppercase;color:#0d9488;margin-bottom:7px;}" +
                "h1{margin:0;font-size:25px;line-height:1.15;color:#0f172a;text-transform:uppercase;letter-spacing:.02em;}" +
                ".hero-note{color:#64748b;font-size:12px;margin-top:6px;line-height:1.45;}" +
                ".doc-reference-box{min-width:270px;border:1px solid #0f766e;padding:10px 12px;display:grid;gap:7px;background:#fbfffe;}" +
                ".doc-reference-box div{display:flex;justify-content:space-between;gap:12px;font-size:11px;border-bottom:1px dotted #bddbd6;padding-bottom:5px;}" +
                ".doc-reference-box div:last-child{border-bottom:0;padding-bottom:0;}" +
                ".doc-reference-box span{color:#0f766e;font-weight:800;text-transform:uppercase;}" +
                ".doc-reference-box strong{color:#0f172a;text-align:right;}" +
                ".info-layout{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:16px;padding:18px 28px 0;}" +
                ".info-card,.meta-item,.metric-card,.visual-card,.table-card{background:#fff;border:1px solid #cbdedb;padding:12px 14px;}" +
                ".card-title{font-size:12px;font-weight:900;letter-spacing:.08em;text-transform:uppercase;color:#064e3b;margin-bottom:10px;padding-bottom:6px;border-bottom:2px solid #0f766e;}" +
                ".info-grid{display:grid;gap:8px;}" +
                ".info-grid-2{grid-template-columns:repeat(2,minmax(0,1fr));}" +
                ".info-row,.meta-row{display:grid;grid-template-columns:minmax(110px,.9fr) minmax(0,1.1fr);gap:8px;align-items:start;}" +
                ".info-row span,.meta-row span,.meta-item span,.metric-card span{display:block;font-size:10px;font-weight:900;letter-spacing:.08em;text-transform:uppercase;color:#0f766e;}" +
                ".info-row strong,.meta-row strong,.meta-item strong,.metric-card strong{font-size:13px;color:#0f172a;font-weight:800;word-break:break-word;}" +
                ".meta-grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:12px;padding:16px 28px 0;}" +
                ".metric-grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:12px;padding:16px 28px 0;}" +
                ".metric-grid-3{grid-template-columns:repeat(3,minmax(0,1fr));}" +
                ".metric-card strong{display:block;margin-top:6px;font-size:16px;}" +
                ".visual-section{padding:16px 28px 0;display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:16px;}" +
                ".visual-card h3{margin:0 0 6px;font-size:14px;color:#064e3b;text-transform:uppercase;}" +
                ".visual-card p{margin:0 0 10px;color:#64748b;font-size:11px;line-height:1.45;}" +
                ".chart-stack{display:grid;gap:10px;}" +
                ".chart-head{display:flex;justify-content:space-between;gap:12px;font-size:11px;color:#111827;margin-bottom:5px;}" +
                ".bar-track{height:11px;background:#e6fffa;overflow:hidden;display:flex;border:1px solid #bddbd6;}" +
                ".bar-fill{height:100%;}" +
                ".bar-fill.primary{background:#0d9488;}.bar-fill.warning{background:#f59e0b;}.bar-fill.info{background:#0f766e;}.bar-fill.danger{background:#b91c1c;}" +
                ".legend-row{display:flex;justify-content:space-between;gap:12px;margin-top:8px;font-size:11px;color:#0f172a;border-top:1px dotted #bddbd6;padding-top:6px;}" +
                ".section{padding:20px 28px 8px;}" +
                ".section-title{font-size:16px;font-weight:900;margin-bottom:10px;color:#064e3b;text-transform:uppercase;border-bottom:2px solid #0f766e;padding-bottom:5px;}" +
                "table{width:100%;border-collapse:collapse;font-size:11px;border-top:2px solid #0f766e;border-bottom:2px solid #0f766e;}" +
                "th,td{padding:7px 8px;text-align:left;border-bottom:1px solid #d7e7e4;vertical-align:top;}" +
                "th{background:#ecfdf5;font-size:10px;font-weight:900;letter-spacing:.05em;text-transform:uppercase;color:#064e3b;border-bottom:2px solid #0f766e;}" +
                "td:nth-child(5),td:nth-child(6),td:nth-child(7),th:nth-child(5),th:nth-child(6),th:nth-child(7){text-align:right;}" +
                "tbody tr:nth-child(even){background:#fbfbfc;}" +
                ".empty-row{text-align:center;color:#64748b;padding:20px 12px;}" +
                ".signature-strip{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:18px;padding:24px 28px 0;}" +
                ".signature-box{border-top:1px solid #0f766e;padding:9px 8px 0;text-align:center;min-height:48px;}" +
                ".signature-box span{display:block;font-size:9px;font-weight:900;letter-spacing:.12em;text-transform:uppercase;color:#0f766e;margin-bottom:5px;}" +
                ".signature-box strong{font-size:12px;color:#0f172a;}" +
                ".doc-footer{display:flex;justify-content:space-between;gap:16px;align-items:center;padding:14px 28px 18px;color:#0f172a;font-size:10px;border-top:1px solid #0f766e;margin-top:18px;background:#fbfffe;}" +
                ".doc-footer::after{content:'Computer generated document - signature is not required unless manually authorized.';font-weight:800;font-style:italic;}" +
                "@page{size:A4 landscape;margin:9mm;}" +
                "@media print{body{background:#fff;padding:0;}.doc-shell{box-shadow:none;border:none;}.doc-header,.doc-title-row,.meta-grid,.metric-grid,.visual-section,.section,.signature-strip,.doc-footer,.info-layout{padding-left:18px;padding-right:18px;}th,.doc-accent,.doc-chip{-webkit-print-color-adjust:exact;print-color-adjust:exact;}}";
    }

    private Path resolveWorkspaceRoot() {
        Path current = Paths.get("").toAbsolutePath().normalize();
        if (current.getFileName() != null && "stable-sbms-backend".equalsIgnoreCase(current.getFileName().toString())) {
            Path parent = current.getParent();
            if (parent != null) {
                return parent;
            }
        }
        return current;
    }
}
