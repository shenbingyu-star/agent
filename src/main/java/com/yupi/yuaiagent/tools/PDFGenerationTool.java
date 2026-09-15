package com.yupi.yuaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.yupi.yuaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * PDF 生成工具
 */
public class PDFGenerationTool {

    @Tool(description = "Generate a PDF file with given content", returnDirect = false)
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 创建 PdfWriter 和 PdfDocument 对象
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                PdfFont font = createChineseFont();
                document.setFont(font);
                for (String line : content.split("\\r?\\n", -1)) {
                    document.add(new Paragraph(line.isEmpty() ? " " : line).setFont(font));
                }
            }
            return "PDF generated successfully to: " + filePath;
        } catch (IOException e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }

    private PdfFont createChineseFont() throws IOException {
        String[] localFonts = {
                "C:\\Windows\\Fonts\\msyh.ttc,0",
                "C:\\Windows\\Fonts\\simsun.ttc,0",
                "C:\\Windows\\Fonts\\simhei.ttf"
        };
        for (String fontPath : localFonts) {
            try {
                return PdfFontFactory.createFont(
                        fontPath,
                        PdfEncodings.IDENTITY_H,
                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (Exception ignored) {
                // try next
            }
        }
        return PdfFontFactory.createFont(
                "STSong-Light",
                "UniGB-UCS2-H",
                PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
    }
}
