package ru.vsu.cs.leonov_m.cg_4;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import ru.vsu.cs.leonov_m.cg_4.modules.LZWComp;
import ru.vsu.cs.leonov_m.cg_4.modules.LZWDecomp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class HelloController {
    @FXML
    ImageView inputImage;
    @FXML
    ImageView outputImage;
    @FXML
    Label inputPath;
    @FXML
    Button inputButton;
    @FXML
    Button saveButton;
    @FXML
    Button runButton;

    @FXML
    private void inputButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\miha4\\Images"));
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));

        File file = fileChooser.showOpenDialog(inputButton.getScene().getWindow());
        inputPath.setText(file.getAbsolutePath());

        Image image = new Image(file.toURI().toString());
        inputImage.setImage(image);
   }

   @FXML
    private void saveButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\miha4\\Images"));

        fileChooser.setInitialFileName("result");

        fileChooser.getExtensionFilters()
               .addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg"));

        File file = fileChooser.showSaveDialog(outputImage.getScene().getWindow());
        BufferedImage image = SwingFXUtils.fromFXImage(outputImage.getImage(), null);
        BufferedImage imag2e = SwingFXUtils.fromFXImage(inputImage.getImage(), null);

       System.out.println(image.toString());
       System.out.println(imag2e.toString());

        try {
            System.out.println("t2");
            System.out.println(ImageIO.write(image, "png", file));
            System.out.println("t3");
        } catch (IOException e) {
            System.out.println("t4");
            throw new RuntimeException(e);
        }
   }

    @FXML
    private void runButtonAction() throws IOException {
        Image old = inputImage.getImage();

        byte[] array = byteArray(SwingFXUtils.fromFXImage(old, null));

        LZWComp lzwComp = new LZWComp();
        byte[] out = lzwComp.compress(array);

        LZWDecomp lzwDecomp = new LZWDecomp();
        byte[] result = lzwDecomp.decompress(out);

        Image nw = null;
        try {
            nw = convertToFxImage(ImageIO.read(new ByteArrayInputStream(result)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        outputImage.setImage(nw);
    }

    @FXML
    private void saveLZWAction() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\miha4\\Images"));


        fileChooser.setInitialFileName("result");


        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("Binary file", "*.lzw"));

        File file = fileChooser.showSaveDialog(outputImage.getScene().getWindow());

        Image old = inputImage.getImage();

        byte[] array = byteArray(SwingFXUtils.fromFXImage(old, null));

        LZWComp lzwComp= new LZWComp();
        byte[] out = lzwComp.compress(array);

        RandomAccessFile r = new RandomAccessFile(file, "rw");
        r.write(out);
        r.close();
    }

    public byte[] byteArray(BufferedImage image){
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        byte[] imageInByte = null;
        try{
            ImageIO.write(image, "JPG", bas);
            bas.flush();
            imageInByte = bas.toByteArray();
            bas.close();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }

        return imageInByte;
    }

    private static Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }

        return new ImageView(wr).getImage();
    }
}