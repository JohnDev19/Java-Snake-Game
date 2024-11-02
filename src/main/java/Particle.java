import java.awt.*;
import java.awt.geom.*;

public class Particle {
    private double x, y;
    private double xVel, yVel;
    private double size;
    private double alpha;
    private Color color;

    public Particle(double x, double y) {
        this.x = x;
        this.y = y;
        this.xVel = (Math.random() - 0.5) * 8;
        this.yVel = (Math.random() - 0.5) * 8;
        this.size = Math.random() * 6 + 2;
        this.alpha = 1.0;
        this.color = new Color(255, 69, 0);
    }

    public boolean update() {
        x += xVel;
        y += yVel;
        size *= 0.95;
        alpha *= 0.95;
        return alpha < 0.1;
    }

    public void draw(Graphics2D g2d) {
        Color particleColor = new Color(
            color.getRed()/255f,
            color.getGreen()/255f,
            color.getBlue()/255f,
            (float)alpha
        );
        g2d.setColor(particleColor);
        g2d.fill(new Ellipse2D.Double(x - size/2, y - size/2, size, size));
    }
}