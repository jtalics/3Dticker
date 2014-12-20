package com.jtalics.ticker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;

public class Ticker extends JPanel implements MouseListener
{
    public static enum Origin
    {
        Right(),
        Left(),
        Top(),
        Bottom();
        Origin()
        {
        }
    }

    public static Origin origin = Origin.Right;
    int length;
    int lastPix;
    int w, h;
    BufferedImage image;
    Graphics2D imageG;
    Random r;
    Timer timer;
    static Font font = new Font(Font.SANS_SERIF, Font.BOLD, 48);
    private List<TChar> tchars = new ArrayList<TChar>();
    List<TChar> offCanvas = new ArrayList<TChar>();
    FontRenderContext frc;
    private boolean initialized = false;

    private Ticker(int seed)
    {
        r = new Random(seed);
        lastPix = r.nextInt(4) + 1;
    }

    private Ticker initialize()
    {
        w = Ticker.this.getWidth();
        h = Ticker.this.getHeight();
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        imageG = image.createGraphics();
        imageG.setFont(font);
        frc = ((Graphics2D) getGraphics()).getFontRenderContext();
        setBackground(Color.BLACK);
        addMouseListener(this);
        initialized = true;
        return this;
    }

    private void loadNextTChar()
    {
        // Assuming all TChars have been transformed, get the position of the last one
        // and add the new characters that will draw on the canvas.
        char newChar = randomChar();
        TChar newTChar = new TChar(this, newChar);

        if (tchars.size() == 0)
        {
            TChar lastChar = new TChar(this, newChar);
            switch (origin)
            {
                case Bottom:
                    lastChar.y = h - 1;
                    break;
                case Left:
                    lastChar.x = -(int)lastChar.bounds.getWidth();
                    break;
                case Right:
                    lastChar.x = w - 1;
                    break;
                case Top:
                    lastChar.y = -(int)lastChar.bounds.getHeight();
                    break;
            }
            tchars.add(lastChar);
        }
        else
        {
            TChar lastChar = tchars.get(tchars.size() - 1);
            // Is char waiting to be drawn?
            switch (origin)
            {
                case Bottom:
                    if (lastChar.y <= h)
                    {
                        newTChar.y = lastChar.y + (int)lastChar.bounds.getHeight();
                        tchars.add(newTChar);
                        length++;
                    }
                    break;
                case Left:
                    if (lastChar.x >= 0)
                    {
                        newTChar.x = lastChar.x - (int)newTChar.bounds.getWidth();
                        tchars.add(newTChar);
                        length++;
                    }
                    break;
                case Right:
                    if (lastChar.x <= w)
                    {
                        newTChar.x = lastChar.x + (int)lastChar.bounds.getWidth();
                        tchars.add(newTChar);
                        length++;
                    }
                    break;
                case Top:
                    if (lastChar.y > 0)
                    {
                        newTChar.y = lastChar.y - (int)newTChar.bounds.getHeight();
                        tchars.add(newTChar);
                        length++;
                    }
                    break;
            }
        }
    }


    private char randomChar()
    {
        // http://www.math.cornell.edu/~mec/2003-2004/cryptography/subs/frequencies.html
        float roll = r.nextFloat() * 100.0f;
        if (roll < 0.07)
            return 'z';
        if (roll < 0.17)
            return 'j';
        if (roll < 0.28)
            return 'q';
        if (roll < 0.45)
            return 'x';
        if (roll < 1.14)
            return 'k';
        if (roll < 2.25)
            return 'v';
        if (roll < 3.74)
            return 'b';
        if (roll < 5.56)
            return 'p';
        if (roll < 7.59)
            return 'g';
        if (roll < 9.68)
            return 'w';
        if (roll < 11.79)
            return 'y';
        if (roll < 14.09)
            return 'f';
        if (roll < 16.70)
            return 'm';
        if (roll < 19.41)
            return 'c';
        if (roll < 22.29)
            return 'u';
        if (roll < 26.27)
            return 'l';
        if (roll < 30.59)
            return 'd';
        if (roll < 36.51)
            return 'h';
        if (roll < 42.53)
            return 'r';
        if (roll < 48.81)
            return 's';
        if (roll < 55.76)
            return 'n';
        if (roll < 63.07)
            return 'i';
        if (roll < 70.75)
            return 'o';
        if (roll < 78.87)
            return 'a';
        if (roll < 87.97)
            return 't';
        if (roll <= 100)
            return 'e';
        throw new RuntimeException();
    }
    char[] cc = new char[1];
    final List<TChar> selected = new ArrayList<TChar>();
    int word;
    final Map<Integer,ArrayList<TChar>> wordToTChars = new HashMap<Integer,ArrayList<TChar>>();

    @Override
    public void paint(Graphics g)
    {
        if (initialized)
        {
            ((Graphics2D) g).drawImage(image, 0, 0, null);
            move();
        }
    }

    public void move()
    {
        if (tchars.size() < -0)
        {
            return; // nothing to draw
        }
        int size;
        imageG.clearRect(0, 0, w, h);
        // Get a character and draw it. If the character moved off the canvas,
        // remove it from the list.
        size = tchars.size();
        for (int i = 0; i < size; i++)
        {
            TChar tchar = tchars.get(i);
            cc[0] = tchar.tchar;
            imageG.setColor(tchar.color);
            imageG.drawChars(cc, 0, 1, (int) tchar.x, (int) tchar.y);
        }
        for (int i = 0; i < size; i++)
        {
            TChar tchar = tchars.get(i);
            tchar.transform();
        }
        tchars.removeAll(offCanvas);
        //System.out.println(tchars.size());;
        offCanvas.clear();
        loadNextTChar();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        for (TChar tchar : tchars) {
            tchar.mouseClicked(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        for (TChar tchar : tchars) {
            tchar.mouseEntered(e);
        }
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        for (TChar tchar : tchars) {
            tchar.mouseExited(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        for (TChar tchar : tchars) {
            tchar.mousePressed(e);
        }
        if (selected.size()<=0) return;
        TChar closest = selected.get(0);
        for (int i=1; i<selected.size(); i++) {
            if (selected.get(i).pix > closest.pix) {
                closest = selected.get(i);
            }
        }
        if (e.getButton() == MouseEvent.BUTTON1) {
            // colorize whole word
            for (TChar tchar : wordToTChars.get(closest.word)) {
                tchar.colorizeWord();
            }
        }
        else if (e.getButton() == MouseEvent.BUTTON3) {
            // colorize whole word
            for (TChar tchar : wordToTChars.get(closest.word)) {
                tchar.reverseWord();
            }
        }
        selected.clear();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        for (TChar tchar : tchars) {
            tchar.mouseReleased(e);
        }
    }

    public void register(int word, TChar tChar)
    {
        if (wordToTChars.get(word) == null) {
            ArrayList<TChar> tCharList = new ArrayList<TChar>();
            wordToTChars.put(word, tCharList);
        }
        wordToTChars.get(word).add(tChar);
    }

    public void deregister(TChar tChar)
    {
        wordToTChars.remove(tChar.word);
        offCanvas.add(tChar);
    }

    public static void main(String[] args)
    {
        class MyPanel extends JPanel implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                repaint();
            }
        }
        JWindow win = new JWindow();
        MyPanel panel = new MyPanel();
        win.getContentPane().add(panel);
        win.setBackground(Color.BLACK);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int n = 0;
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
        int w=metrics.charWidth('M');
        int h=metrics.getAscent()+metrics.getDescent();
        Ticker[] tickers = null;
        switch (origin)
        {
            case Left:
            case Right:
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                n = d.height /  h;
                tickers = new Ticker[n];
                for (int i = 0; i < n; i++)
                {
                    tickers[i] = new Ticker(i);
                    tickers[i].setPreferredSize(new Dimension(d.width, h));
                    panel.add(tickers[i]);
                }
                break;
            case Bottom:
            case Top:
                panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
                n = d.width / w;
                tickers = new Ticker[n];
                for (int i = 0; i < n; i++)
                {
                    tickers[i] = new Ticker(i);
                    tickers[i].setPreferredSize(new Dimension(w,d.height));
                    panel.add(tickers[i]);
                }
                break;
        }
        win.setSize(d);
        win.setVisible(true);
        for (int i = 0; i < n; i++)
        {
            tickers[i].initialize();
            tickers[i].timer = new Timer(50, panel);
            tickers[i].timer.start();
        }
    }
}
