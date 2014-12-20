/**
 * 
 */
package com.cboe.ticker;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

class TChar implements MouseListener
{
    /**
     * 
     */
    private final Ticker ticker;
    int pix = 1;
    final char tchar;
    final Rectangle2D bounds;
    int x;
    int y;
    Color color;
    private char[] cc = new char[1];
    int word;

    // ticker char
    TChar(Ticker ticker, char tchar) // TODO: one param only
    {
        word=ticker.word;
        cc [0] = tchar;
        bounds= Ticker.font.getStringBounds(cc, 0, 1, ticker.frc);

        this.ticker = ticker;
        if (this.ticker.length >= 3 && this.ticker.r.nextInt(10) < 1 )
        {
            do
            {
                this.pix = this.ticker.r.nextInt(4) + 1;
            }
            while (this.pix == this.ticker.lastPix);
            ticker.word++;
            this.ticker.length = 0;
        }
        else
        {
            ticker.register(ticker.word,this);
            pix = this.ticker.lastPix;
        }
        this.ticker.lastPix = this.pix;
        this.tchar = tchar;
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(Ticker.font);
        this.y = metrics.getAscent();
        switch (pix)
        {
            case 4:
                color = Color.WHITE;
                break;
            case 3:
                color = Color.lightGray;
                break;
            case 2:
                color = Color.gray;
                break;
            case 1:
                color = Color.darkGray;
                break;
        }
        //System.out.println(""+tchar+" "+pix);
    }

    public void transform()
    {
        // Move to new place and
        // check if we are off the screen now
        switch (Ticker.origin)
        {
            case Bottom:
                y -= pix;
                if (y + bounds.getHeight() < 0)
                {
                    ticker.deregister(this);
                }
                break;
            case Left:
                x += pix;
                if (x > this.ticker.w)
                {
                    ticker.deregister(this);
                }
                break;
            case Right:
                x -= pix;
                if (x + bounds.getWidth() < 0)
                {
                    ticker.deregister(this);
                }
                break;
            case Top:
                y += pix;
                if (y - bounds.getHeight() > this.ticker.h)
                {
                    ticker.deregister(this);
                }
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        int w=(int)bounds.getWidth();
        int h=(int)bounds.getHeight();
        Rectangle2D rec = new Rectangle(x,0,w,h);
        if (rec.contains(new Point(e.getX(),e.getY()))) {
            ticker.selected.add(this);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }

    public void colorizeWord()
    {
        color=new Color(color.getRed(),0,0);
    }

    public void reverseWord()
    {
        pix*=-1;
    }
}