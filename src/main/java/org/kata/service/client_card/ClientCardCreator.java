package org.kata.service.client_card;

import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.kata.dto.AvatarDto;
import org.kata.dto.IndividualDto;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ClientCardCreator {

    private final PDDocument card;
    private final PDPage pdPage;
    private final PDPageContentStream contentStream;
    private final ClientCardDataParser clientCardDataParser;
    private final ContentStreamWriter write;
    private final ContentStreamDesigner design;

    @SneakyThrows
    public ClientCardCreator() {
        this.card = new PDDocument();
        this.pdPage = new PDPage(PDRectangle.A4);
        card.addPage(pdPage);
        contentStream = new PDPageContentStream(card, pdPage);

        this.clientCardDataParser = new ClientCardDataParser();
        this.write = new ContentStreamWriter(contentStream);
        this.design = new ContentStreamDesigner(contentStream);

    }

    private int width = (int) PDRectangle.A4.getWidth();
    private int height = (int) PDRectangle.A4.getHeight();


    //Indents
    private int leftIndent = 40;
    private int upIndent = 20;
    private int rightIndent = 20;
    //Avatar
    private int avaWidth = 200;
    private int avaHeight = 200;
    private int avaX = leftIndent;
    private int avaY = height - avaHeight - upIndent;

    //Font
    private int fontSize = 14;

    private int subDataFontSize = 12;

    private PDFont font = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);

    private PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);

    //Main data settings
    private int mainDataSpace = 100;

    private int mainDataY = height - fontSize - upIndent;

    //Intervals
    private int mainDataInterval = 30;

    private int sectionDataInterval = 20;
    private int sectionInterval = sectionDataInterval + 20;


    //Colors
    private int[] rgbLavender = new int[]{230, 230, 250};
    private int[] white = new int[]{255, 255, 255};
    private int[] seaShell = new int[]{245, 245, 220};


    @SneakyThrows
    public PDDocument create(IndividualDto ind) {

        Map<String, String> mainData = clientCardDataParser.parseMainData(ind);
        Map<String, List<String>> subData = clientCardDataParser.parseSubData(ind);

        design.addBackground(2, 2, (float) width - 4, (float) height - 4, seaShell, rgbLavender, 2);

        writeAva(ind.getAvatar().get(0));
        writeMain(mainData);
        writeSub(subData);

        contentStream.close();

        return card;
    }

    private void writeAva(AvatarDto avatarDto) throws IOException {
        PDImageXObject ava = PDImageXObject.createFromByteArray(card, avatarDto.getImageData(), "ava.jpg");
        design.addBackground(avaX, avaY, avaWidth, avaHeight, white, rgbLavender, 0.1f);
        contentStream.drawImage(ava, (float) avaX + 2, (float) avaY + 2, (float) avaWidth - 4, (float) avaHeight - 4);
    }

    private void writeMain(Map<String, String> mainData) {
        write.setOffset(avaWidth + leftIndent + 30, mainDataY);

        mainData.forEach(this::writeMainDataLine);
    }

    private void writeMainDataLine(String key, String value) {
        design.addBackground((float) write.x() - 5, (float) write.y() - 5,
                (float) width - write.x() - rightIndent + 5,
                (float) fontSize + 5, white, rgbLavender, 0.1f);
        int x = fontSize + mainDataSpace;
        write.writeString(key, Color.BLACK, boldFont, fontSize);
        write.x(write.x() + x);
        write.writeString(value, Color.BLACK, font, fontSize);
        write.setOffset(write.x() - x, write.y() - mainDataInterval);

    }

    private void writeSub(Map<String, List<String>> subData) {

        write.setOffset(leftIndent + 5, height - (avaHeight + upIndent));

        subData.forEach((section, data) -> {

            writeSection(section, data.size());
            design.drawLine(leftIndent, write.y() - 5, rgbLavender, width - rightIndent, 0.1f);
            writeSectionData(data);
        });
    }

    private void writeSection(String section, int size) {
        write.y(write.y() - sectionInterval);
        addSectionBackground(size);
        write.writeString(section, Color.BLACK, boldFont, fontSize);
    }

    private void writeSectionData(List<String> strings) {
        write.x(write.x() + 15);
        for (String string : strings) {
            write.y(write.y() - sectionDataInterval);
            write.writeString(string, Color.BLACK, font, subDataFontSize);
        }
        write.x(write.x() - 15);

    }

    private void addSectionBackground(int lines) {
        int sectionSize = lines * sectionDataInterval;

        float x = leftIndent;
        float y = write.y() - sectionSize - (float) sectionInterval / 4;
        float w = (float) this.width - leftIndent - rightIndent;
        float h = sectionSize + (float)(sectionInterval * 0.7);

        design.addBackground(x, y, w, h, white, rgbLavender, 0.1f);
    }
}
