package org.kata.service.client_card;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;

public class ContentStreamWriter {

    private PDPageContentStream cs;

    @Getter
    private int[] offset;

    public ContentStreamWriter(PDPageContentStream pdContentStream) {
        this.cs = pdContentStream;
        setOffset(0,0);
    }

    @SneakyThrows
    public void writeString(String string, Color color, PDFont font, int fontsize) {
        cs.setNonStrokingColor(color);
        cs.beginText();
        cs.newLineAtOffset(offset[0], offset[1]);
        cs.setFont(font, fontsize);
        cs.showText(string);
        cs.endText();
    }
    public void setOffset(int x, int y) {
        this.offset = new int[] {x,y};
    }
    public void x(int x) {
        this.offset[0] = x;
    }
    public void y(int y) {
        this.offset[1] = y;
    }
    public int x() {
        return offset[0];
    }
    public int y() {
        return offset[1];
    }
}
