package cn.coonu.common.pdf;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PDFUtil {

    public static byte[] toPDF(byte[]... imageBytes) {
        List<Image> imageList = new ArrayList<>(imageBytes.length);
        for (byte[] imageByte : imageBytes) {
            imageList.add(new Image(ImageDataFactory.create(imageByte)));
        }
        return toPDF(imageList);
    }

    /**
     * 将所有图片组合成一个A4页面PDF文件, 且所有图片需要同比例缩放到页面大小, 且需要竖放居中;
     *
     * @param images {@link Image}对象;
     * @return PDF文件byte流;
     */
    @SuppressWarnings("all")
    private static byte[] toPDF(List<Image> images) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
        Document document = new Document(pdfDocument, PageSize.A4);

        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            float height = PageSize.A4.getHeight();
            float width = PageSize.A4.getWidth();
            //如果图片不是竖着的, 则旋转图片
            if (image.getImageHeight() < image.getImageWidth()) {
                //需要旋转;
                image.setRotationAngle(Math.PI / 2);
                image.scaleToFit(height, width);
                image.setFixedPosition(i + 1, (width - image.getImageScaledHeight()) / 2, (height - image.getImageScaledWidth()) / 2);
            } else {
                //不需要旋转
                image.scaleToFit(width, height);
                image.setFixedPosition(i + 1, (width - image.getImageScaledWidth()) / 2, (height - image.getImageScaledHeight()) / 2);
            }
            pdfDocument.addNewPage(PageSize.A4);
            document.add(image);
        }

        document.close();

        return outputStream.toByteArray();
    }

}
