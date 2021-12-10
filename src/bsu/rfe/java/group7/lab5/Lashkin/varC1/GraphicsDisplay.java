package bsu.rfe.java.group7.lab5.Lashkin.varC1;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.util.EmptyStackException;
import java.util.Stack;

import javax.swing.JPanel;

@SuppressWarnings({ "serial" })
public class GraphicsDisplay extends JPanel {

    class GraphPoint {
        double xd;
        double yd;
        int x;
        int y;
        int n;
    }

    class Zone {
        double MAXY;
        double tmp;
        double MINY;
        double MAXX;
        double MINX;
        boolean use;
    }

    private Zone zone = new Zone();
    private double[][] graphicsData;
    private int[][] graphicsDataI;
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean transform = false;
    private boolean showGrid = true;
    private boolean PPP = false;
    private boolean zoom=false;
    private boolean selMode = false;
    private boolean dragMode = false;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double scale;
    private double scaleX;
    private double scaleY;
    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
    private BasicStroke graphicsStroke;
    private BasicStroke gridStroke;
    private BasicStroke hatchStroke;
    private BasicStroke axisStroke;
    private BasicStroke selStroke;
    private BasicStroke markerStroke;
    private Font axisFont;
    private Font captionFont;
    private int mausePX = 0;
    private int mausePY = 0;
    private GraphPoint SMP;
    double xmax;
    private Rectangle2D.Double rect;
    private Stack<Zone> stack = new Stack<Zone>();


    public GraphicsDisplay() {
        setBackground(Color.WHITE);
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] { 8, 2, 2, 2, 4, 2, 2, 2, 8, 2 }, 0.0f);
        gridStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, null, 0.0f);
        hatchStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, null, 0.0f);
        axisStroke = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        selStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 8, 8 }, 0.0f);
        axisFont = new Font("Serif", Font.BOLD, 20);
        captionFont = new Font("Serif", Font.BOLD, 10);
        MouseMotionHandler mouseMotionHandler = new MouseMotionHandler();
        addMouseMotionListener(mouseMotionHandler);
        addMouseListener(mouseMotionHandler);
        rect = new Rectangle2D.Double();
        zone.use = false;
    }

    public void showGraphics(double[][] graphicsData) {
        this.graphicsData = graphicsData;
        graphicsDataI = new int[graphicsData.length][2];
        repaint();
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setTransform(boolean transform) {
        this.transform = transform;
        repaint();
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        repaint();
    }

    public int getDataLenght() {
        return graphicsData.length;
    }

    public double getValue(int i, int j) {
        return graphicsData[i][j];
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graphicsData == null || graphicsData.length == 0)
            return;
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        if (zone.use) {
            minX = zone.MINX;
        }
        if (zone.use) {
            maxX = zone.MAXX;
        }
        minY = graphicsData[0][1];
        maxY = minY;
        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1] > maxY) {
                maxY = graphicsData[i][1];
                xmax = graphicsData[i][1];
            }
        }
        if (zone.use) {
            minY = zone.MINY;
        }
        if (zone.use) {
            maxY = zone.MAXY;
        }
        scaleX = 1.0 / (maxX - minX);
        scaleY = 1.0 / (maxY - minY);
        if (!transform)
            scaleX *= getSize().getWidth();
        else
            scaleX *= getSize().getHeight();
        if (!transform)
            scaleY *= getSize().getHeight();
        else
            scaleY *= getSize().getWidth();
        if (transform) {
            ((Graphics2D) g).rotate(-Math.PI / 2);
            ((Graphics2D) g).translate(-getHeight(), 0);
        }
        scale = Math.min(scaleX, scaleY);
        if(!zoom){
            if (scale == scaleX) {
                double yIncrement = 0;
                if (!transform)
                    yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;
                else
                    yIncrement = (getSize().getWidth() / scale - (maxY - minY)) / 2;
                maxY += yIncrement;
                minY -= yIncrement;
            }
            if (scale == scaleY) {
                double xIncrement = 0;
                if (!transform) {
                    xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 2;
                    maxX += xIncrement;
                    minX -= xIncrement;
                } else {
                    xIncrement = (getSize().getHeight() / scale - (maxX - minX)) / 2;
                    maxX += xIncrement;
                    minX -= xIncrement;
                }
            }
        }
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();
        if (showGrid)
            paintGrid(canvas);
        if (showAxis)
            paintAxis(canvas);
        paintGraphics(canvas);
        if (showMarkers)
            paintMarkers(canvas);
        if (SMP != null)
            paintHint(canvas);
        if (selMode) {
            canvas.setColor(Color.BLACK);
            canvas.setStroke(selStroke);
            canvas.draw(rect);
        }
        canvas.drawString ("maxY", (int)xyToPoint(0, xmax).x+5, (int)xyToPoint(0, xmax).y+5);
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    protected void paintHint(Graphics2D canvas) {
        Color oldColor = canvas.getColor();
        canvas.setColor(Color.MAGENTA);
        StringBuffer label = new StringBuffer();
        label.append("X=");
        label.append(formatter.format((SMP.xd)));
        label.append(", Y=");
        label.append(formatter.format((SMP.yd)));
        FontRenderContext context = canvas.getFontRenderContext();
        Rectangle2D bounds = captionFont.getStringBounds(label.toString(),context);
        if (!transform) {
            int dy = -10;
            int dx = +7;
            if (SMP.y < bounds.getHeight())
                dy = +13;
            if (getWidth() < bounds.getWidth() + SMP.x + 20)
                dx = -(int) bounds.getWidth() - 15;
            canvas.drawString (label.toString(), SMP.x + dx, SMP.y + dy);
        } else {
            int dy = 10;
            int dx = -7;
            if (SMP.x < 10)
                dx = +13;
            if (SMP.y < bounds.getWidth() + 20)
                dy = -(int) bounds.getWidth() - 15;
            canvas.drawString (label.toString(), getHeight() - SMP.y + dy, SMP.x + dx);
        }
        canvas.setColor(oldColor);
    }

    protected void paintGraphics(Graphics2D canvas) {
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.BLACK);
        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < graphicsData.length; i++) {
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            graphicsDataI[i][0] = (int) point.getX();
            graphicsDataI[i][1] = (int) point.getY();
            if (transform) {
                graphicsDataI[i][0] = (int) point.getY();
                graphicsDataI[i][1] = getHeight() - (int) point.getX();
            }
            if (i > 0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }
        canvas.draw(graphics);
    }

    protected void paintHatch(Graphics2D canvas, double x1, double y1,
                              double step) {// @!
        Color oldColor = canvas.getColor();
        Stroke oldStroke = canvas.getStroke();
        canvas.setColor(Color.GRAY);
        canvas.setStroke(hatchStroke);
        GeneralPath graphics = new GeneralPath();
        int uu = 0;
        int y = (int) xyToPoint(0, 0).getY();
        int x;
        int d = 0;
        for (double i = x1 + step; i < maxX; i += step) {
            uu++;
            if (uu == 5) {
                uu = -5;
                d = 5;
            } else
                d = 0;
            x = (int) xyToPoint(i, 0).getX();
            if (!transform) {
                if (x > getWidth() - 22)
                    break;
            } else {
                if (x > getHeight() - 22)
                    break;
            }
            graphics.moveTo(x, y - 5 - d);
            graphics.lineTo(x, y + 5 + d);
        }
        uu = 0;
        for (double i = x1 - step; i > minX; i -= step) {
            uu++;
            if (uu == 5) {
                uu = -5;
                d = 5;
            } else
                d = 0;
            x = (int) xyToPoint(i, 0).getX();
            graphics.moveTo(x, y - 5 - d);
            graphics.lineTo(x, y + 5 + d);
        }
        x = (int) xyToPoint(0, 0).getX();
        uu = 0;
        for (double i = y1 + step; i < maxY; i += step) {
            uu++;
            if (uu == 5) {
                uu = -5;
                d = 5;
            } else
                d = 0;
            y = (int) xyToPoint(0, i).getY();
            if (y < 20)
                break;
            graphics.moveTo(x - 5 - d, y);
            graphics.lineTo(x + 5 + d, y);
        }
        uu = 0;
        for (double i = y1 - step; i > minY; i -= step) {
            uu++;
            if (uu == 5) {
                uu = -5;
                d = 5;
            } else
                d = 0;
            y = (int) xyToPoint(0, i).getY();
            graphics.moveTo(x - 5 - d, y);
            graphics.lineTo(x + 5 + d, y);
        }
        canvas.draw(graphics);
        canvas.setStroke(oldStroke);
        canvas.setColor(oldColor);
    }

    private double fix0MAX(final double m) {
        double mm = m;
        int o = 1;
        while (mm < 1.0d) {
            mm = mm * 10;
            o *= 10;
        }
        int i = (int) mm + 1;
        return (double) i / o;
    }

    private double fix1MAX(final double m) {
        double mm = m;
        int o = 1;
        while (mm > 1.0d) {
            mm = mm / 10;
            o *= 10;
        }
        mm *= 10;
        int i = (int) mm + 1;
        o /= 10;
        return (double) i * o;
    }

    protected void paintCaptions(Graphics2D canvas, double step) {
        formatter.setMaximumFractionDigits(5);
        formatter.setGroupingUsed(false);
        DecimalFormatSymbols dottedDouble = formatter.getDecimalFormatSymbols();
        dottedDouble.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(dottedDouble);
        Color oldColor = canvas.getColor();
        Stroke oldStroke = canvas.getStroke();
        Font oldFont = canvas.getFont();
        canvas.setColor(Color.BLACK);
        canvas.setFont(captionFont);
        int xp = (int) xyToPoint(0, 0).x;
        int yp;
        FontRenderContext context = canvas.getFontRenderContext();
        double y = step;
        while (y <= maxY) {
            yp = (int) xyToPoint(0, y).y;
            if (yp < 30)
                break;
            String xs = formatter.format(y);
            Rectangle2D bounds = captionFont.getStringBounds(xs, context);
            canvas.drawString (xs, (int) (xp - 5 - bounds.getWidth()), yp);
            y += step;
        }
        y = -step;

        while (y >= minY) {
            yp = (int) xyToPoint(0, y).y;
            String xs = formatter.format(y);
            Rectangle2D bounds = captionFont.getStringBounds(xs, context);
            canvas.drawString (xs, (int) (xp - 5 - bounds.getWidth()), yp);
            y -= step;
        }

        double x = 0.0d + step;
        yp = (int) xyToPoint(0, 0).y;
        while (x <= maxX) {

            xp = (int) xyToPoint(x, 0).x;
            String xs = formatter.format(x);
            Rectangle2D bounds = captionFont.getStringBounds(xs, context);
            if (!transform) {
                if (xp + (int) (bounds.getWidth() / 2) > getWidth())
                    break;
            } else {
                if (xp + bounds.getWidth() > getHeight())
                    break;
            }
            canvas.drawString (xs, xp - (int) (bounds.getWidth() / 2), yp + 20);
            x += step;
        }
        x = -step;
        while (x >= minX) {
            xp = (int) xyToPoint(x, 0).x;
            String xs = formatter.format(x);
            Rectangle2D bounds3 = captionFont.getStringBounds(xs, context);
            if (xp - (int) (bounds3.getWidth() / 2) < 0)
                break;
            canvas.drawString (xs, xp - (int) (bounds3.getWidth() / 2), yp + 20);
            x -= step;
        }
        canvas.drawString ("0", (int) xyToPoint(0, 0).getX() + 5,
                (int) xyToPoint(0, 0).getY() + 20);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
        canvas.setFont(oldFont);
    }

    protected void paintGrid(Graphics2D canvas) {
        GeneralPath graphics = new GeneralPath();
        double MAX = Math.max(Math.abs(maxX - minX), Math.abs(maxY - minY));
        double MAX20 = MAX / 20;
        double step = 0.0f;
        if (MAX20 < 1)
            step = fix0MAX(MAX20);
        else
            step = fix1MAX(MAX20);
        if (PPP) {
            int YY = Math.min(getWidth(), getHeight());
            if (YY < 200)
                step *= 3;
            else if (YY < 400)
                step *= 2;
        }
        Color oldColor = canvas.getColor();
        Stroke oldStroke = canvas.getStroke();
        canvas.setStroke(gridStroke);
        canvas.setColor(Color.BLUE);
        int xp = 0;
        double x = 0.0d;
        int gH = getHeight();
        int gW = getWidth();
        if (transform) {
            gH = getWidth();
            gW = getHeight();
        }
        xp = (int) xyToPoint(0, 0).x;
        while (xp > 0) {
            graphics.moveTo(xp, 0);
            graphics.lineTo(xp, gH);
            xp = (int) xyToPoint(x, 0).x;
            x -= step;
        }
        xp = (int) xyToPoint(0, 0).x;

        while (xp < gW) {
            graphics.moveTo(xp, 0);
            graphics.lineTo(xp, gH);
            xp = (int) xyToPoint(x, 0).x;
            x += step;
        }
        int yp = (int) xyToPoint(0, 0).y;
        double y = 0.0f;
        while (yp < gH) {
            yp = (int) xyToPoint(0, y).y;
            graphics.moveTo(0, yp);
            graphics.lineTo(gW, yp);
            y -= step;
        }
        yp = (int) xyToPoint(0, 0).y;
        while (yp > 0) {
            yp = (int) xyToPoint(0, y).y;
            graphics.moveTo(0, yp);
            graphics.lineTo(gW, yp);
            y += step;
        }
        canvas.draw(graphics);
        paintHatch(canvas, 0, 0, step / 10);
        paintCaptions(canvas, step);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    private boolean markPoint(double y) {
        int n = (int) y;
        if (n < 0)
            n *= (-1);
        while (n != 0) {
            int q = n - (n / 10) * 10;
            if (q % 2 != 0)
                return false;
            n = n / 10;
        }
        return true;
    }

    protected void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);
        for (double[] point : graphicsData) {
            if (markPoint(point[1])) {
                canvas.setColor(Color.BLACK);
            } else {
                canvas.setColor(Color.GREEN);
            }
            GeneralPath path = new GeneralPath();
            Point2D.Double center = xyToPoint(point[0], point[1]);
            path.moveTo(center.x - 5, center.y + 0);
            path.lineTo(center.x + 5, center.y + 0);
            path.moveTo(center.x + 0, center.y + 5);
            path.lineTo(center.x + 0, center.y - 5);
            path.moveTo(center.x + 5, center.y + 5);
            path.lineTo(center.x - 5, center.y - 5);
            path.moveTo(center.x - 5, center.y + 5);
            path.lineTo(center.x + 5, center.y - 5);
            canvas.draw(path);
        }
    }

    protected void paintAxis(Graphics2D canvas){
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);
        FontRenderContext context = canvas.getFontRenderContext();



        if (minX <= 0.0 && maxX >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0.1, maxY-0.1), xyToPoint(0.1, minY-0.1)));

            GeneralPath arrow = new GeneralPath();

            Point2D.Double lineEnd = xyToPoint(0.1, maxY-0.1);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());

            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);

            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());


            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0.1, maxY-0.1);

            canvas.drawString("y", (float) labelPos.getX() + 10, (float) (labelPos.getY() - bounds.getY()));

        }

        canvas.draw(new Line2D.Double(xyToPoint(0, minY+0.1), xyToPoint(maxX, minY+0.1)));

        GeneralPath arrow = new GeneralPath();

        Point2D.Double lineEnd = xyToPoint(maxX, minY+0.1);
        arrow.moveTo(lineEnd.getX(), lineEnd.getY());

        arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);

        arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);


        arrow.closePath();
        canvas.draw(arrow);
        canvas.fill(arrow);

        Rectangle2D bounds = axisFont.getStringBounds("x", context);
        Point2D.Double labelPos = xyToPoint(maxX, minY+0.1);

        canvas.drawString("x", (float) (labelPos.getX() - bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));

    }

    protected Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        if(!zoom)
            return new Point2D.Double(deltaX * scale, deltaY * scale);
        else
            return new Point2D.Double(deltaX * scaleX, deltaY * scaleY);
    }


    protected Point2D.Double pointToXY(int x, int y) {
        Point2D.Double p = new Point2D.Double();
        if (!transform) {
            p.x = x / scale + minX;
            int q = (int) xyToPoint(0, 0).y;
            p.y = maxY - maxY * ((double) y / (double) q);
        } else {
            if(!zoom){
                p.y = -x / scale + (maxY);
                p.x = -y / scale + maxX;
            }else{
                p.y = -x / scaleY + (maxY);
                p.x = -y / scaleX + maxX;
            }
        }
        return p;
    }

    public class MouseMotionHandler implements MouseMotionListener, MouseListener {
        private double comparePoint(Point p1, Point p2) {
            return Math.sqrt(Math.pow(p1.x - p2.x, 2)
                    + Math.pow(p1.y - p2.y, 2));
        }

        private GraphPoint find(int x, int y) {
            GraphPoint smp = new GraphPoint();
            GraphPoint smp2 = new GraphPoint();
            double r, r2 = 1000;
            if (graphicsData!=null) {
                for (int i = 0; i < graphicsData.length; i++) {
                    Point p = new Point();
                    p.x = x;
                    p.y = y;
                    Point p2 = new Point();
                    p2.x = graphicsDataI[i][0];
                    p2.y = graphicsDataI[i][1];
                    r = comparePoint(p, p2);
                    if (r < 7.0) {
                        smp.x = graphicsDataI[i][0];
                        smp.y = graphicsDataI[i][1];
                        smp.xd = graphicsData[i][0];
                        smp.yd = graphicsData[i][1];
                        smp.n = i;
                        if (r < r2) {
                            r2 = r;
                            smp2 = smp;
                        }
                        return smp2;
                    }
                }
            }
            return null;
        }

        public void mouseMoved(MouseEvent ev) {
            if(ev!=null){
                GraphPoint smp;
                smp = find(ev.getX(), ev.getY());
                if (smp != null) {
                    setCursor(Cursor.getPredefinedCursor(8));
                    SMP = smp;
                } else {
                    setCursor(Cursor.getPredefinedCursor(0));
                    SMP = null;
                }
                repaint();
            }
        }

        public void mouseDragged(MouseEvent e) {
            if (selMode) {
                if (!transform)
                    rect.setFrame(mausePX, mausePY, e.getX() - rect.getX(),
                            e.getY() - rect.getY());
                else {
                    rect.setFrame(-mausePY + getHeight(), mausePX, -e.getY()
                            + mausePY, e.getX() - mausePX);
                }
                repaint();
            }
            if (dragMode) {
                if (!transform) {
                    if(pointToXY(e.getX(), e.getY()).y<maxY && pointToXY(e.getX(), e.getY()).y>minY){
                        graphicsData[SMP.n][1] = pointToXY(e.getX(), e.getY()).y;
                        SMP.yd = pointToXY(e.getX(), e.getY()).y;
                        SMP.y = e.getY();
                    }
                } else {
                    if(pointToXY(e.getX(), e.getY()).y<maxY && pointToXY(e.getX(), e.getY()).y>minY){
                        graphicsData[SMP.n][1] = pointToXY(e.getX(), e.getY()).y;
                        SMP.yd = pointToXY(e.getX(), e.getY()).y;
                        SMP.x = e.getX();
                    }
                }
                repaint();
            }
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getButton() != 3)
                return;

            try {
                zone = stack.pop();
            } catch (EmptyStackException err) {

            }
            if(stack.empty())
                zoom=false;
            repaint();
        }

        public void mouseEntered(MouseEvent arg0) {

        }

        public void mouseExited(MouseEvent arg0) {

        }

        public void mousePressed(MouseEvent e) {
            if (e.getButton() != 1)
                return;
            if (SMP != null) {
                selMode = false;
                dragMode = true;
            } else {
                dragMode = false;
                selMode = true;
                mausePX = e.getX();
                mausePY = e.getY();
                if (!transform)
                    rect.setFrame(e.getX(), e.getY(), 0, 0);
                else
                    rect.setFrame(e.getX(), e.getY(), 0, 0);
            }
        }

        public void mouseReleased(MouseEvent e) {
            rect.setFrame(0, 0, 0, 0);
            if (e.getButton() != 1) {
                repaint();
                return;
            }
            if (selMode) {
                if (!transform) {
                    if (e.getX() <= mausePX || e.getY() <= mausePY)
                        return;
                    int eY = e.getY();
                    int eX = e.getX();
                    if (eY > getHeight())
                        eY = getHeight();
                    if (eX > getWidth())
                        eX = getWidth();
                    double MAXX = pointToXY(eX, 0).x;
                    double MINX = pointToXY(mausePX, 0).x;
                    double MAXY = pointToXY(0, mausePY).y;
                    double MINY = pointToXY(0, eY).y;
                    stack.push(zone);
                    zone = new Zone();
                    zone.use = true;
                    zone.MAXX = MAXX;
                    zone.MINX = MINX;
                    zone.MINY = MINY;
                    zone.MAXY = MAXY;
                    selMode = false;
                    zoom=true;
                } else {
                    if (pointToXY(mausePX, 0).y <= pointToXY(e.getX(), 0).y
                            || pointToXY(0, e.getY()).x <= pointToXY(0, mausePY).x)
                        return;
                    int eY = e.getY();
                    int eX = e.getX();
                    if (eY < 0)
                        eY = 0;
                    if (eX > getWidth())
                        eX = getWidth();
                    stack.push(zone);
                    zone = new Zone();
                    zone.use = true;
                    zone.MAXY = pointToXY(mausePX, 0).y;
                    zone.MAXX = pointToXY(0, eY).x;
                    zone.MINX = pointToXY(0, mausePY).x;
                    zone.MINY = pointToXY(eX, 0).y;
                    selMode = false;
                    zoom=true;
                }

            }
            repaint();
        }
    }
}