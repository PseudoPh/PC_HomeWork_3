import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class HW3 {

    public static final int WIDTH = 1600;
    public static final int HEIGHT = 1200;
    public static final int MAX_ITER = 1000;
    public static final int THREADS = 4;

    public static final double X_MIN = -2.2;
    public static final double X_MAX = 1.0;
    public static final double Y_MIN = -1.2;
    public static final double Y_MAX = 1.2;

    static boolean inSet(int x, int y) {
        double cRe = X_MIN + (X_MAX - X_MIN) * x / WIDTH;
        double cIm = Y_MIN + (Y_MAX - Y_MIN) * y / HEIGHT;
        double zRe = 0;
        double zIm = 0;

        for (int i = 0; i < MAX_ITER; i++) {
            double newRe = zRe * zRe - zIm * zIm + cRe;
            zIm = 2 * zRe * zIm + cIm;
            zRe = newRe;

            if (zRe * zRe + zIm * zIm > 4) {
                return false;
            }
        }
        return true;
    }

    static Thread createThread(int number, boolean[][] result) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                for (int y = number; y < HEIGHT; y += THREADS) {
                    for (int x = 0; x < WIDTH; x++) {
                        result[y][x] = inSet(x, y);
                    }
                }
            }
        };
        return new Thread(task);
    }

    static void saveImage(boolean[][] result, String fileName) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (result[y][x]) {
                    image.setRGB(x, y, 0x000000);
                } else {
                    image.setRGB(x, y, 0xFFFFFF);
                }
            }
        }
        ImageIO.write(image, "png", new File(fileName));
    }

    public static void main(String[] args) throws Exception {
        boolean[][] result = new boolean[HEIGHT][WIDTH];
        Thread[] threads = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++) {
            threads[i] = createThread(i, result);
        }
        for (int i = 0; i < THREADS; i++) {
            threads[i].start();
        }
        for (int i = 0; i < THREADS; i++) {
            threads[i].join();
        }

        saveImage(result, "mandelbrot.png");
    }
}