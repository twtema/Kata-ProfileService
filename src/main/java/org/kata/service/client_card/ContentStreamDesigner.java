package org.kata.service.client_card;

import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDPageContentStream;


public class ContentStreamDesigner {

    private final PDPageContentStream cs;


    public ContentStreamDesigner(PDPageContentStream pdPageContentStream) {
        this.cs = pdPageContentStream;
    }

    @SneakyThrows
    void drawLine(int startX, int y, int[] rgb, int endX, float lineWidth) {
        setLineColor(rgb);
        cs.setLineWidth(lineWidth);
        cs.moveTo(startX, y);
        cs.lineTo(endX, y);
        cs.stroke();
    }
    @SneakyThrows
    void setColor(int[] rgb) {
        cs.setNonStrokingColor(rgb[0]/255.0f, rgb[1]/255.0f, rgb[2]/255.0f);
    }
    @SneakyThrows
    void setLineColor(int[] rgb) {
        cs.setStrokingColor(rgb[0]/255.0f, rgb[1]/255.0f, rgb[2]/255.0f);
    }
    @SneakyThrows
    void addBackground (float x, float y, float width, float height, int[]rgb, int[] borderRGB, float borderWidth) {
        setColor(rgb);
        setLineColor(borderRGB);
        cs.setLineWidth(borderWidth);
        cs.addRect(x, y, width, height);
        cs.fillAndStroke();
    }
}
