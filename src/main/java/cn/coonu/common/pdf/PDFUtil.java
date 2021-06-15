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

    /**
     * 将所有图片组合成一个A4页面PDF文件, 且所有图片需要同比例缩放到页面大小, 且需要竖放居中;
     *
     * @param imageBytes 多张图片byte流;
     * @return PDF文件byte流;
     */
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

        //大概流程: 创建文档 - 配置image属性 - 添加页面 - 添加图片

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
        //将文档页面大小默认值设置为A4, 如果后面没有自定义, 所有页面都将使用此大小;
        Document document = new Document(pdfDocument, PageSize.A4);

        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            float height = PageSize.A4.getHeight();
            float width = PageSize.A4.getWidth();
            if (image.getImageHeight() < image.getImageWidth()) {
                //如果图片不是竖着的, 则旋转图片
                image.setRotationAngle(Math.PI / 2);
                //将图片同比例缩放, 适合页面大小;
                image.scaleToFit(height, width);
                //设置图片位置与所在页码;
                image.setFixedPosition(i + 1, (width - image.getImageScaledHeight()) / 2, (height - image.getImageScaledWidth()) / 2);
            } else {
                //不需要旋转
                image.scaleToFit(width, height);
                image.setFixedPosition(i + 1, (width - image.getImageScaledWidth()) / 2, (height - image.getImageScaledHeight()) / 2);
            }
            // 此行可以不用, 因为添加image时如果没有指定页码, 将会自动添加页面, 且默认页面大小在Document处就已经配置, 就是A4;
            // 除非想单独将此页面设置成图片同样大小可以自定义设置, 那么页面大小将以此处为准;
            pdfDocument.addNewPage(PageSize.A4);
            document.add(image);
        }

        document.close();

        return outputStream.toByteArray();
    }

}
