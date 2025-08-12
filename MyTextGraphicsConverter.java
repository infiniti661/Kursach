package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class MyTextGraphicsConverter implements TextGraphicsConverter {
    private double maxRatio = 0;
    private int maxWidth = 0;
    private int maxHeight = 0;
    private TextColorSchema schema = new MyTextColorSchema();

    @Override
    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    @Override
    public void setMaxRatio(double ratio) {
        this.maxRatio = ratio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));

        int width = img.getWidth();
        int height = img.getHeight();

        // 1. Проверка соотношения сторон
        double ratio = (double) Math.max(width, height) / Math.min(width, height);
        if (maxRatio > 0 && ratio > maxRatio) {
            throw new BadImageSizeException(ratio, maxRatio);
        }

        // 2. Масштабирование при превышении лимитов
        double scale = 1.0;
        if (maxWidth > 0 && width > maxWidth) {
            scale = (double) maxWidth / width;
        }
        if (maxHeight > 0 && height > maxHeight) {
            scale = Math.min(scale, (double) maxHeight / height);
        }
        if (scale != 1.0) {
            width = (int) (width * scale);
            height = (int) (height * scale);
        }

        Image scaledImage = img.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
        BufferedImage bwImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g = bwImg.createGraphics();
        g.drawImage(scaledImage, 0, 0, null);

        WritableRaster raster = bwImg.getRaster();
        StringBuilder sb = new StringBuilder();

        int[] pixel = new int[3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = raster.getPixel(x, y, pixel)[0];
                char c = schema.convert(color);
                sb.append(c).append(c); // удваиваем символ для нормальных пропорций
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}

