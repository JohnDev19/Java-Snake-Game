import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int UNIT_SIZE = 20;
    private static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 75;

    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyParts = 6;
    private int foodEaten;
    private int foodX;
    private int foodY;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private Random random;

    // Colors and effects constants
    private static final Color BACKGROUND_COLOR = new Color(20, 30, 40);
    private static final Color SNAKE_HEAD_COLOR = new Color(50, 205, 50);
    private static final Color SNAKE_BODY_COLOR = new Color(34, 139, 34);
    private static final Color FOOD_COLOR = new Color(255, 69, 0);
    private static final Color GRID_COLOR = new Color(40, 50, 60);

    // Animation
    private float animationAngle = 0;
    private float glowEffect = 0;
    private boolean glowIncreasing = true;

    // Particle
    private ArrayList<Particle> particles = new ArrayList<>();

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(BACKGROUND_COLOR);
        this.setFocusable(true);
        this.addKeyListener(this);
        startGame();
    }

    public void startGame() {
        newFood();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2d);

        if (running) {
            drawSnake(g2d);
            drawFood(g2d);
            drawParticles(g2d);
            drawScore(g2d);
        } else {
            gameOver(g2d);
        }
    }

    private void drawBackground(Graphics2D g2d) {
        // Draw gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, BACKGROUND_COLOR,
            WIDTH, HEIGHT, new Color(40, 50, 60)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw grid
        g2d.setColor(GRID_COLOR);
        for (int i = 0; i < WIDTH; i += UNIT_SIZE) {
            g2d.drawLine(i, 0, i, HEIGHT);
        }
        for (int i = 0; i < HEIGHT; i += UNIT_SIZE) {
            g2d.drawLine(0, i, WIDTH, i);
        }
    }

    private void drawSnake(Graphics2D g2d) {
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                // Draw snake head / glow effect
                float alpha = 0.5f + (glowEffect * 0.5f);
                Color glowColor = new Color(
                    SNAKE_HEAD_COLOR.getRed()/255f,
                    SNAKE_HEAD_COLOR.getGreen()/255f,
                    SNAKE_HEAD_COLOR.getBlue()/255f,
                    alpha
                );

                // Draw glow
                g2d.setColor(glowColor);
                g2d.fill(new Ellipse2D.Double(
                    x[i] - 5, y[i] - 5,
                    UNIT_SIZE + 10, UNIT_SIZE + 10
                ));

                // Draw head
                g2d.setColor(SNAKE_HEAD_COLOR);
                g2d.fill(new RoundRectangle2D.Double(
                    x[i], y[i],
                    UNIT_SIZE, UNIT_SIZE,
                    10, 10
                ));

                // Draw eyes
                g2d.setColor(Color.WHITE);
                switch(direction) {
                    case 'R':
                        drawEyes(g2d, x[i] + UNIT_SIZE*3/4, y[i] + UNIT_SIZE/4);
                        break;
                    case 'L':
                        drawEyes(g2d, x[i] + UNIT_SIZE/4, y[i] + UNIT_SIZE/4);
                        break;
                    case 'U':
                        drawEyes(g2d, x[i] + UNIT_SIZE/4, y[i] + UNIT_SIZE/4);
                        break;
                    case 'D':
                        drawEyes(g2d, x[i] + UNIT_SIZE/4, y[i] + UNIT_SIZE*3/4);
                        break;
                }
            } else {
                // Draw snake body
                g2d.setColor(SNAKE_BODY_COLOR);
                g2d.fill(new RoundRectangle2D.Double(
                    x[i], y[i],
                    UNIT_SIZE-2, UNIT_SIZE-2,
                    8, 8
                ));
            }
        }
    }

    private void drawEyes(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x-2, y-2, 6, 6);
        g2d.fillOval(x+8, y-2, 6, 6);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x, y, 2, 2);
        g2d.fillOval(x+10, y, 2, 2);
    }

    private void drawFood(Graphics2D g2d) {
        // Draw food
        double scale = 1.0 + Math.sin(animationAngle) * 0.1;

        // Draw glow effect
        Color glowColor = new Color(
            FOOD_COLOR.getRed()/255f,
            FOOD_COLOR.getGreen()/255f,
            FOOD_COLOR.getBlue()/255f,
            0.3f
        );
        g2d.setColor(glowColor);
        g2d.fill(new Ellipse2D.Double(
            foodX - (UNIT_SIZE*scale)/4, foodY - (UNIT_SIZE*scale)/4,
            UNIT_SIZE*scale*1.5, UNIT_SIZE*scale*1.5
        ));

        // Draw food
        g2d.setColor(FOOD_COLOR);
        g2d.fill(new Ellipse2D.Double(
            foodX, foodY,
            UNIT_SIZE*scale, UNIT_SIZE*scale
        ));
    }

    private void drawParticles(Graphics2D g2d) {
    for (int i = particles.size() - 1; i >= 0; i--) {
        Particle p = particles.get(i);
        if (p.update()) {
            particles.remove(i);
        } else {
            p.draw(g2d);
        }
    }
}

private void drawScore(Graphics2D g2d) {
    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Arial", Font.BOLD, 40));
    FontMetrics metrics = getFontMetrics(g2d.getFont());
    String scoreText = "Score: " + foodEaten;
    g2d.drawString(scoreText, (WIDTH - metrics.stringWidth(scoreText)) / 2, 50);
}

private void gameOver(Graphics2D g2d) {
    // Game Over
    GradientPaint textGradient = new GradientPaint(
        0, HEIGHT/2 - 50,
        Color.RED,
        0, HEIGHT/2 + 50,
        new Color(150, 0, 0)
    );
    g2d.setPaint(textGradient);
    g2d.setFont(new Font("Arial", Font.BOLD, 75));
    FontMetrics metrics1 = getFontMetrics(g2d.getFont());
    g2d.drawString("Game Over", (WIDTH - metrics1.stringWidth("Game Over")) / 2, HEIGHT/2);

    // Final Score
    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Arial", Font.BOLD, 40));
    FontMetrics metrics2 = getFontMetrics(g2d.getFont());
    String scoreText = "Score: " + foodEaten;
    g2d.drawString(scoreText, (WIDTH - metrics2.stringWidth(scoreText)) / 2, HEIGHT/2 + 50);

    // Press Space to Restart
    g2d.setFont(new Font("Arial", Font.PLAIN, 20));
    String restartText = "Press SPACE to Restart";
    FontMetrics metrics3 = getFontMetrics(g2d.getFont());
    g2d.drawString(restartText, (WIDTH - metrics3.stringWidth(restartText)) / 2, HEIGHT/2 + 100);
}

    private void spawnFoodParticles() {
        for (int i = 0; i < 20; i++) {
            particles.add(new Particle(foodX + UNIT_SIZE/2, foodY + UNIT_SIZE/2));
        }
    }

    public void newFood() {
        foodX = random.nextInt((WIDTH/UNIT_SIZE)) * UNIT_SIZE;
        foodY = random.nextInt((HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {
            bodyParts++;
            foodEaten++;
            spawnFoodParticles();
            newFood();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    private void updateAnimations() {
        animationAngle += 0.1;
        if (animationAngle > 2 * Math.PI) {
            animationAngle = 0;
        }

        if (glowIncreasing) {
            glowEffect += 0.05f;
            if (glowEffect >= 1.0f) {
                glowIncreasing = false;
            }
        } else {
            glowEffect -= 0.05f;
            if (glowEffect <= 0.0f) {
                glowIncreasing = true;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
            updateAnimations();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R') {
                    direction = 'L';
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') {
                    direction = 'R';
                }
                break;
            case KeyEvent.VK_UP:
                if (direction != 'D') {
                    direction = 'U';
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') {
                    direction = 'D';
                }
                break;
            case KeyEvent.VK_SPACE:
                if (!running) {
                    resetGame();
                }
                break;
        }
    }

    private void resetGame() {
        bodyParts = 6;
        foodEaten = 0;
        direction = 'R';
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        particles.clear();
        startGame();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}

