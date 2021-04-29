package gui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFrame;
import model.Position;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class LanternaGUI implements GUI {
    private final TerminalScreen screen;
    private final TextGraphics graphics;

    public LanternaGUI(int width, int height) throws IOException, FontFormatException, URISyntaxException {
        AWTTerminalFontConfiguration fontConfig = loadSquareFont();
        Terminal terminal = createTerminal(width, height, fontConfig);
        screen = createScreen(terminal);
        graphics = screen.newTextGraphics();
    }

    private TerminalScreen createScreen(Terminal terminal) throws IOException {
        final TerminalScreen screen;
        screen = new TerminalScreen(terminal);

        screen.setCursorPosition(null);
        screen.startScreen();
        screen.doResizeIfNecessary();
        return screen;
    }

    private Terminal createTerminal(int width, int height, AWTTerminalFontConfiguration fontConfig) throws IOException {
        TerminalSize terminalSize = new TerminalSize(width, height);
        Terminal terminal = new DefaultTerminalFactory()
                .setInitialTerminalSize(terminalSize)
                .setForceAWTOverSwing(true)
                .setTerminalEmulatorFontConfiguration(fontConfig)
                .createTerminal();

        ((AWTTerminalFrame)terminal).addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
        });

        return terminal;
    }

    private AWTTerminalFontConfiguration loadSquareFont() throws URISyntaxException, FontFormatException, IOException {
        URL resource = getClass().getClassLoader().getResource("square.ttf");
        File fontFile = new File(resource.toURI());
        Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);

        Font loadedFont = font.deriveFont(Font.PLAIN, 25);
        AWTTerminalFontConfiguration fontConfig = AWTTerminalFontConfiguration.newInstance(loadedFont);
        return fontConfig;
    }

    @Override
    public void clear() {
        screen.clear();
    }

    @Override
    public void refresh() throws IOException {
        screen.refresh();
    }

    @Override
    public void close() throws IOException {
        screen.close();
    }

    @Override
    public void setBackgroundColor(String color) {
        graphics.setBackgroundColor(TextColor.Factory.fromString(color));

    }

    @Override
    public void setForegroundColor(String color) {
        graphics.setForegroundColor(TextColor.Factory.fromString(color));
    }

    @Override
    public String getBackGroundColor(int x, int y) {
        return graphics.getCharacter(x, y).getBackgroundColor().toString();
    }

    @Override
    public String getForegroundColor(int x, int y) {
        return graphics.getCharacter(x, y).getForegroundColor().toString();
    }

    @Override
    public void drawChar(int x, int y, char character) {
        graphics.setCharacter(x, y, character);
    }

    private boolean isKeyStrokeType(KeyStroke keyStroke, KeyType type) {
        return keyStroke.getKeyType() == type;
    }

    private boolean isKeyStrokeCharacter(KeyStroke keyStroke, char c) {
        return isKeyStrokeType(keyStroke, KeyType.Character) && keyStroke.getCharacter() == c;
    }

    public ACTION getNextAction() throws IOException {
        KeyStroke keyStroke = screen.readInput();

        if (isKeyStrokeType(keyStroke, KeyType.EOF)) return ACTION.QUIT;
        if (isKeyStrokeCharacter(keyStroke, 'q')) return ACTION.QUIT;

        if (isKeyStrokeCharacter(keyStroke, 'w')) return ACTION.UP;
        if (isKeyStrokeCharacter(keyStroke, 'd')) return ACTION.RIGHT;
        if (isKeyStrokeCharacter(keyStroke, 's')) return ACTION.DOWN;
        if (isKeyStrokeCharacter(keyStroke, 'a')) return ACTION.LEFT;

        return ACTION.NONE;
    }

    /*
    private void drawCharacter(int x, int y, char c) {
        graphics.setCharacter(x, y, c);
    }

    private void drawCharacter(Position position, char c) {
        graphics.setCharacter(position.getX(), position.getY(), c);
    }

    private void setColor(TextColor backgroundColor, TextColor foregroundColor) {
        graphics.setBackgroundColor(backgroundColor);
        graphics.setForegroundColor(foregroundColor);
    }


    @Override
    public void drawFarmer(Position position) {
        graphics.setBackgroundColor(TextColor.Factory.fromString("#000000"));
        graphics.setForegroundColor(TextColor.Factory.fromString("#999999"));
        drawCharacter(position, '@');
    }
    */
}
