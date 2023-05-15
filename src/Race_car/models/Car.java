package Race_car.models;

import Race_car.views.GamePanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.RenderingHints;
import java.util.ArrayList;

/**
 * Author: ThachPham
 * Website: https://tpgamecoding.com
 */
public class Car {
    private ArrayList<Rectangle> rectangles;
    public float x = 200, y = 200;
    private int width = 40, height = 60;

    public float angle = -90;

    float acelerationInput = 0;

    private Vector2 vector2;

    private BufferedImage carSprite;

    private TrailRenderer trailRenderer1, trailRenderer2;

    RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    public Car() {
        trailRenderer1 = new TrailRenderer(this, 20, 30);
        trailRenderer2 = new TrailRenderer(this, 20, -30);
        rectangles = new ArrayList<>();

        rectangles.add(new Rectangle(0, 0, 120, 100));
        rectangles.add(new Rectangle(0, 260, 120, 90));
        rectangles.add(new Rectangle(0, 520, 120, 200));
        rectangles.add(new Rectangle(250, 0, 350, 100));
        rectangles.add(new Rectangle(250, 260, 350, 90));
        rectangles.add(new Rectangle(250, 520, 350, 200));
        rectangles.add(new Rectangle(720, 0, 200, 100));
        rectangles.add(new Rectangle(720, 260, 200, 90));
        rectangles.add(new Rectangle(720, 520, 200, 200));

        try {
            carSprite = ImageIO.read(new File("/Users/johntoan98gmail.com/Downloads/Demo_Xe/src/Race_car/img/car.png"));
        } catch (IOException e) {
        }
    }

    public void draw(Graphics g) {
        trailRenderer1.render(g);
        trailRenderer2.render(g);
        float angleToDraw = angle + 90;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(rh);
        g2d.rotate(Math.toRadians(angleToDraw), x, y);
        g.setColor(Color.RED);
        g.drawLine((int) x, (int) y, (int) x, (int) y - 50);
//		g.fillRect((int) (x - width / 2), (int) (y - height / 2), width, height);
        g.drawImage(carSprite, (int) (x - width / 2), (int) (y - height / 2), width, height, null);

        g2d.rotate(Math.toRadians(-angleToDraw), x + width / 2, y + height / 2);

        if (vector2 != null) {
            g.setColor(Color.BLACK);
            g.drawLine((int) x, (int) y, (int) (x + vector2.x), (int) (y + vector2.y));
        }
    }

    private boolean collidedWithRectangle = true;

    public void update() {
        if (GamePanel.reset) {
            x = 200;
            y = 200;
            GamePanel.reset = false;
        }
        Vector2 inputVector = Vector2.zero();
        inputVector.x = Input.getAxis("horizontal");
        inputVector.y = Input.getAxis("vertical");

        if (inputVector.x > 1) inputVector.x = 1;
        if (inputVector.x < -1) inputVector.x = -1;

        // One is negative & one is positive
        if (inputVector.y * acelerationInput < 0) {
            System.out.println("brake");
            trailRenderer1.emitting = true;
            trailRenderer2.emitting = true;
        } else {
            trailRenderer1.emitting = false;
            trailRenderer2.emitting = false;
        }

        angle += inputVector.x * 4;

        if (inputVector.y != 0)
            acelerationInput += inputVector.y;
        else if (acelerationInput > 0)
            acelerationInput -= 0.03;
        else if (acelerationInput < 0)
            acelerationInput += 0.03;
        if (acelerationInput < -7) acelerationInput = -7;
        else if (acelerationInput > 5) acelerationInput = 5;

        Vector2 engineForceVector = Vector2.zero();
        engineForceVector.y = acelerationInput;
        vector2 = engineForceVector = transformVectorByAngle(engineForceVector);

        float nextX = x + vector2.x;
        float nextY = y + vector2.y;
        float carHalfWidth = width / 2;
        float carHalfHeight = height / 2;
        float borderLeft = carHalfWidth;
        float borderRight = 900 - carHalfWidth;
        float borderTop = carHalfHeight;
        float borderBottom = 680 - carHalfHeight;

        if (nextX >= borderLeft && nextX <= borderRight && nextY >= borderTop && nextY <= borderBottom) {
            // Kiểm tra va chạm với các hình chữ nhật
            for (Rectangle rectangle : rectangles) {
                if (nextX + carHalfWidth > rectangle.getX() &&
                        nextX - carHalfWidth < rectangle.getX() + rectangle.getWidth() &&
                        nextY + carHalfHeight > rectangle.getY() &&
                        nextY - carHalfHeight < rectangle.getY() + rectangle.getHeight()) {
                    // Ô tô va chạm với hình chữ nhật, dừng lại
                    if (collidedWithRectangle) {
                        GamePanel.numberOfCollisions--;
                    }
                    collidedWithRectangle = false;
                    return;
                }
            }

            // Ô tô không va chạm với hình chữ nhật, di chuyển bình thường

            x = nextX;
            y = nextY;
            collidedWithRectangle = true;
        } else {
            // Ô tô va chạm với viền, dừng lại
            if (collidedWithRectangle) {
                GamePanel.numberOfCollisions--;
            }
            collidedWithRectangle = false;
            return;
        }


        trailRenderer1.update();
        trailRenderer2.update();
    }

    public void addForce(Vector2 vector2) {
        x += vector2.x;
        y += vector2.y;
    }

    private Vector2 transformVectorByAngle(Vector2 input) {
        Vector2 vector = Vector2.zero();
        float radian = (angle) * 3.14f / 180;
//		System.out.println(angle + ", " + radian);
        vector.x = input.x * (float) Math.sin(radian) - input.y * (float) Math.cos(radian);
        vector.y = input.x * (float) Math.cos(radian) - input.y * (float) Math.sin(radian);
        return vector;
    }


}

