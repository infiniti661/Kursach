package ru.netology.graphics.image;

public class MyTextColorSchema implements TextColorSchema {
    private final char[] symbols = {'#', '$', '@', '%', '*', '+', '-', '\''};

    @Override
    public char convert(int color) {
        int index = (color * (symbols.length - 1)) / 255;
        return symbols[index];
    }
}
