package xd.wsy.common;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Vector;

public class BookPageFactory {

    private static final String TAG = "BookPageFactory";
    private File book_file = null;
    private int m_backColor = 0xFFCEEBCD; // 背景颜色
    private Bitmap m_book_bg = null;
    private int m_fontSize = 25;
    private boolean m_isfirstPage, m_islastPage;
    private Vector<String> m_lines = new Vector<String>();
    private MappedByteBuffer m_mbBuf = null;// 内存中的图书字符
    private int m_mbBufBegin = 0;// 当前页起始位置
    private int m_mbBufEnd = 0;// 当前页终点位置

    private int m_mbBufLen = 10; // 图书总长度

    private String m_strCharsetName = "UTF-8";

    private int m_textColor = Color.rgb(28, 28, 28);

    private int marginHeight = 50; // 上下与边缘的距离
    private int marginWidth = 15; // 左右与边缘的距离
    private int mHeight;
    private int mLineCount; // 每页可以显示的行数
    private Paint mPaint;

    private float mVisibleHeight; // 绘制内容的宽
    private float mVisibleWidth; // 绘制内容的宽
    private int mWidth;

    public BookPageFactory(int w, int h) {
        mWidth = w;
        mHeight = h;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
        mPaint.setTextAlign(Paint.Align.LEFT);//设置绘制文字的对齐方向
        mPaint.setTextSize(m_fontSize);// 字体大小
        mPaint.setColor(m_textColor);// 字体颜色
        mVisibleWidth = mWidth - marginWidth * 2;
        mVisibleHeight = mHeight - marginHeight * 2;
        mLineCount = (int) (mVisibleHeight / m_fontSize) - 1; // 可显示的行数,-1是因为底部显示进度的位置容易被遮住
    }

    public boolean isfirstPage() {
        return m_isfirstPage;
    }

    public boolean islastPage() {
        return m_islastPage;
    }

    public void currentPage() throws IOException {
        m_lines.clear();
        m_lines = pageDown();
    }

    public void onDraw(Canvas c) {
        mPaint.setTextSize(m_fontSize);
        mPaint.setColor(m_textColor);
        if (m_lines.size() == 0)
            m_lines = pageDown();
        if (m_lines.size() > 0) {
            if (m_book_bg == null)
                c.drawColor(m_backColor);
            else
                c.drawBitmap(m_book_bg, 0, 0, null);
            int y = marginHeight;
            for (String strLine : m_lines) {
                y += m_fontSize;
                //从（x,y）坐标将文字绘于手机屏幕
                c.drawText(strLine, marginWidth, y, mPaint);
            }
        }

        //计算百分比（不包括当前页）并格式化
        float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);

        @SuppressLint("DrawAllocation")
        DecimalFormat df = new DecimalFormat("#0.0");
        String strPercent = df.format(fPercent * 100) + "%";

        //计算999.9%所占的像素宽度
        int nPercentWidth = (int) mPaint.measureText("999.9%") + 1;
        c.drawText(strPercent, mWidth - nPercentWidth, mHeight - 5, mPaint);
    }

    /**
     * @param strFilePath
     * @param begin
     *            表示书签记录的位置，读取书签时，将begin值给m_mbBufEnd，在读取nextpage，及成功读取到了书签
     *            记录时将m_mbBufBegin开始位置作为书签记录
     *
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public void openBook(String strFilePath, int begin) throws IOException {
        book_file = new File(strFilePath);
        long lLen = book_file.length();
        m_mbBufLen = (int) lLen;

        /*
         * 内存映射文件能让你创建和修改那些因为太大而无法放入内存的文件。有了内存映射文件，你就可以认为文件已经全部读进了内存，
         * 然后把它当成一个非常大的数组来访问。这种解决办法能大大简化修改文件的代码。
         * fileChannel.map(FileChannel.MapMode mode, long position, long size)将此通道的文件区域直接映射到内存中。但是，你必
         * 须指明，它是从文件的哪个位置开始映射的，映射的范围又有多大
         */

        m_mbBuf = new RandomAccessFile(book_file, "r").getChannel().map(FileChannel.MapMode.READ_ONLY, 0, lLen);
        Log.d(TAG, "total lenth：" + m_mbBufLen);
        // 设置已读进度
        if (begin >= 0) {
            m_mbBufBegin = begin;
            m_mbBufEnd = begin;
        }
    }

    /**
     * 画指定页的下一页
     *
     * @return 下一页的内容 Vector<String>
     */
    private Vector<String> pageDown() {
        mPaint.setTextSize(m_fontSize);
        mPaint.setColor(m_textColor);
        String strParagraph = "";
        Vector<String> lines = new Vector<String>();
        while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
            byte[] paraBuf = readParagraphForward(m_mbBufEnd);  //读取一个段落
            m_mbBufEnd += paraBuf.length;// 每次读取后，记录结束点位置，该位置是段落结束位置    //结束位置后移paraBuf.length
            try {
                strParagraph = new String(paraBuf, m_strCharsetName);// 转换成制定GBK编码
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "pageDown->转换编码失败", e);
            }
            String strReturn = "";
            // 替换掉回车换行符      //去除字符串中的特殊字符
            if (strParagraph.contains("\r\n")) {
                strReturn = "\r\n";
                strParagraph = strParagraph.replaceAll("\r\n", "");
            } else if (strParagraph.contains("\n")) {
                strReturn = "\n";
                strParagraph = strParagraph.replaceAll("\n", "");
            }

            if (strParagraph.length() == 0) {
                lines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                // 画一行文字          //计算每行可以显示多少个字符
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
                lines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);// 得到剩余的文字   //截取从nSize开始的字符串
                // 超出最大行数则不再画
                if (lines.size() >= mLineCount) {
                    break;
                }
            }
            // 如果该页最后一段只显示了一部分，则从新定位结束点位置      //当前页没显示完
            if (strParagraph.length() != 0) {
                try {
                    m_mbBufEnd -= (strParagraph + strReturn).getBytes(m_strCharsetName).length;
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "pageDown->记录结束点位置失败", e);
                }
            }
        }
        return lines;
    }

    /**
     * 得到上上页的结束位置
     */
    private void pageUp() {
        if (m_mbBufBegin < 0)
            m_mbBufBegin = 0;
        Vector<String> lines = new Vector<String>();
        String strParagraph = "";
        while (lines.size() < mLineCount && m_mbBufBegin > 0) {
            Vector<String> paraLines = new Vector<String>();
            byte[] paraBuf = readParagraphBack(m_mbBufBegin);
            m_mbBufBegin -= paraBuf.length;// 每次读取一段后,记录开始点位置,是段首开始的位置
            try {
                strParagraph = new String(paraBuf, m_strCharsetName);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "pageUp->转换编码失败", e);
            }
            strParagraph = strParagraph.replaceAll("\r\n", "");
            strParagraph = strParagraph.replaceAll("\n", "");
            // 如果是空白行，直接添加
            if (strParagraph.length() == 0) {
                paraLines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                // 画一行文字
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
                paraLines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);
            }
            lines.addAll(0, paraLines);
        }

        while (lines.size() > mLineCount) {
            try {
                m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
                lines.remove(0);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "pageUp->记录起始点位置失败", e);
            }
        }
        m_mbBufEnd = m_mbBufBegin;// 上上一页的结束点等于上一页的起始点
    }

    /**
     * 向前翻页
     *
     * @throws IOException
     */
    public void prePage() throws IOException {
        if (m_mbBufBegin <= 0) {
            m_mbBufBegin = 0;                 //第一页
            m_isfirstPage = true;
            return;
        } else
            m_isfirstPage = false;
        m_lines.clear();
        pageUp();
        m_lines = pageDown();
    }

    /**
     * 向后翻页
     *
     * @throws IOException
     */
    public void nextPage() throws IOException {
        if (m_mbBufEnd >= m_mbBufLen) {
            m_islastPage = true;
            return;
        } else
            m_islastPage = false;
        m_lines.clear();
        m_mbBufBegin = m_mbBufEnd;// 下一页页起始位置=当前页结束位置
        m_lines = pageDown();
    }

    /**
     * 读取指定位置的上一个段落
     *
     * @param nFromPos
     * @return byte[]
     */
    private byte[] readParagraphBack(int nFromPos) {
        int i;
        byte b0, b1;

        //根据编码格式判断换行
        if (m_strCharsetName.equals("UTF-16LE")) {
            i = nFromPos - 2;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                b1 = m_mbBuf.get(i + 1);
                if (b0 == 0x0a && b1 == 0x00 && i != nFromPos - 2) {
                    i += 2;
                    break;
                }
                i--;
            }
        } else if (m_strCharsetName.equals("UTF-16BE")) {
            i = nFromPos - 2;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                b1 = m_mbBuf.get(i + 1);
                if (b0 == 0x00 && b1 == 0x0a && i != nFromPos - 2) {
                    i += 2;
                    break;
                }
                i--;
            }
        } else {
            i = nFromPos - 1;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                if (b0 == 0x0a && i != nFromPos - 1) {// 0x0a表示换行符
                    i++;
                    break;
                }
                i--;
            }
        }
        if (i < 0)
            i = 0;

        //共读取了多少字符
        int nParaSize = nFromPos - i;
        int j;
        byte[] buf = new byte[nParaSize];
        for (j = 0; j < nParaSize; j++) {
            buf[j] = m_mbBuf.get(i + j);         //将已读取的字符放入数组
        }
        return buf;
    }

    /**
     * 读取指定位置的下一个段落
     * @param nFromPos
     * @return byte[]
     */
    private byte[] readParagraphForward(int nFromPos) {
        int i = nFromPos;
        byte b0, b1;
        // 根据编码格式判断换行
        if (m_strCharsetName.equals("UTF-16LE")) {
            while (i < m_mbBufLen - 1) {
                b0 = m_mbBuf.get(i++);
                b1 = m_mbBuf.get(i++);
                if (b0 == 0x0a && b1 == 0x00) {
                    break;
                }
            }
        } else if (m_strCharsetName.equals("UTF-16BE")) {
            while (i < m_mbBufLen - 1) {
                b0 = m_mbBuf.get(i++);
                b1 = m_mbBuf.get(i++);
                if (b0 == 0x00 && b1 == 0x0a) {
                    break;
                }
            }
        } else {
            while (i < m_mbBufLen) {
                b0 = m_mbBuf.get(i++);
                if (b0 == 0x0a) {
                    break;
                }
            }
        }

        //共读取了多少字符
        int nParaSize = i - nFromPos;
        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            //将已读取的字符放入数组
            buf[i] = m_mbBuf.get(nFromPos + i);
        }
        return buf;
    }

    public void setBgBitmap(Bitmap BG) {
        m_book_bg = BG;
    }

    public void setM_fontSize(int m_fontSize) {
        this.m_fontSize = m_fontSize;
        mLineCount = (int) (mVisibleHeight / m_fontSize) - 1;
    }

    // 设置页面起始点
    public void setM_mbBufBegin(int m_mbBufBegin) {
        this.m_mbBufBegin = m_mbBufBegin;
    }

    // 设置页面结束点
    public void setM_mbBufEnd(int m_mbBufEnd) {
        this.m_mbBufEnd = m_mbBufEnd;
    }

    public int getM_mbBufBegin() {
        return m_mbBufBegin;
    }

    public String getFirstLineText() {
        return m_lines.size() > 0 ? m_lines.get(0) : "";
    }

    public int getM_textColor() {
        return m_textColor;
    }

    public void setM_textColor(int m_textColor) {
        this.m_textColor = m_textColor;
    }

    public int getM_mbBufLen() {
        return m_mbBufLen;
    }

    public int getM_mbBufEnd() {
        return m_mbBufEnd;
    }
}
