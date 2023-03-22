package xyz.fengyusong.ffmpeg;

import cn.hutool.core.io.FileUtil;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * <p>从视频中提取webp图片</p>
 *
 * @author fengyusong
 * @version 1.0
 * @createTime 2023/3/21
 */
public class VideoExtractWebp {

    public VideoExtractWebp() {
    }

    /**
     * 将video前{@param second}秒的视频中的帧组合成webp动图
     *
     * @param video  视频文件
     * @param second 秒
     * @return webp格式图片
     */
    public static byte[] getVideoExtractWebp(File video, Integer second) {
        //Frame对象
        Frame frame;
        FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(video);
        FFmpegFrameRecorder fFmpegFrameRecorder = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            fFmpegFrameGrabber.start();
            //使用video高宽建立FFmpegFrameRecorder录制器
            fFmpegFrameRecorder = new FFmpegFrameRecorder(byteArrayOutputStream,
                    fFmpegFrameGrabber.getImageWidth(), fFmpegFrameGrabber.getImageHeight(), 0);
            //计算需要获取多少帧的图片
            int fixed = (int) fFmpegFrameRecorder.getFrameRate() * second;
            //设置格式为webp
            fFmpegFrameRecorder.setFormat("webp");
            //设置编码为webp
            fFmpegFrameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_WEBP);
            //设置webp循环播放，“0”代表不限制次数循环播放
            fFmpegFrameRecorder.setOption("loop", "0");

            fFmpegFrameRecorder.start();
            while (fixed > 0) {
                //获取帧
                frame = fFmpegFrameGrabber.grabImage();
                if (frame != null) {
                    fFmpegFrameRecorder.record(frame);
                    fixed--;
                }
            }
            fFmpegFrameGrabber.stop();
            fFmpegFrameRecorder.stop();

        } catch (IOException e) {
            //请根据业务自行处理异常
        } finally {
            try {
                fFmpegFrameGrabber.release();
                if (Objects.nonNull(fFmpegFrameRecorder)) {
                    fFmpegFrameRecorder.releaseUnsafe();
                }
            } catch (IOException e) {
                //请根据业务自行处理异常
            }
        }
        return byteArrayOutputStream.toByteArray();
    }


    public static void main(String[] args) {
        ClassLoader classLoader = VideoExtractWebp.class.getClassLoader();

        //getResource()方法会去classpath下找这个文件，获取到url resource, 得到这个资源后，调用url.getFile获取到 文件 的绝对路径
        URL url = classLoader.getResource("file/video.mp4");
        //url.getFile() 得到这个文件的绝对路径;
        byte[] videoExtractWebp = getVideoExtractWebp(FileUtil.file(url.getFile()), 1);

        FileUtil.writeBytes(videoExtractWebp, "E:\\demo.webp");
    }
}
