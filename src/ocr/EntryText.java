package ocr;

import java.awt.*;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;

/**
 * Created by 122 on 14.04.2017.
 */
public class EntryText extends Entry {
    ArrayList<SampleData> dataText;
    ArrayList<Integer> edges;

    EntryText() {
        super();
    }

    @Override
    public void downSample() {
        final int w = this.entryImage.getWidth(this);
		final int h = this.entryImage.getHeight(this);

        dataText = new ArrayList<>();
        edges = new ArrayList<>();

		final PixelGrabber grabber = new PixelGrabber(this.entryImage, 0, 0, w,
				h, true);
	    try {
            grabber.grabPixels();
			this.pixelMap = (int[]) grabber.getPixels();
			findBounds(w, h);
            findEdges(w);

            SampleData data;
            for(int i = 0; i < edges.size(); i+=2){
                data = new SampleData(' ', OCR.DOWNSAMPLE_WIDTH, OCR.DOWNSAMPLE_HEIGHT);
                //уточним границы для каждого символа
                data.downSampleLeft = edges.get(i);
                data.downSampleRight = edges.get(i+1);
                findHorBounds(data, this.downSampleTop, this.downSampleBottom);
                this.ratioX = (double) (data.downSampleRight - data.downSampleLeft)
                        / (double) data.getWidth();
                this.ratioY = (double) (data.downSampleBottom - data.downSampleTop)
                        / (double) data.getHeight();

                for (int y = 0; y < data.getHeight(); y++) {
                    for (int x = 0; x < data.getWidth(); x++) {
                        if (downSampleRegion(data, x, y)) {
                            data.setData(x, y, true);
                        } else {
                            data.setData(x, y, false);
                        }
                    }
                }

                dataText.add(data);
            }

            repaint();
		} catch (final InterruptedException e) {
		}
    }

    //find horizontal bounds for specialized area - доработай!
    protected void findHorBounds(SampleData data, int top, int bottom) {
        // top line
        for (int y = top; y < bottom; y++) {
            if (!hLineClear(y, data.downSampleLeft, data.downSampleRight)) {
                data.downSampleTop = y;
                break;
            }

        }
        // bottom line
        for (int y = bottom - 1; y >= top; y--) {
            if (!hLineClear(y, data.downSampleLeft, data.downSampleRight)) {
                data.downSampleBottom = y;
                break;
            }
        }
    }

    protected boolean hLineClear(final int y, int left, int right) {
        final int w = this.entryImage.getWidth(this);
        for (int i = left; i < right+1; i++) {
            if (this.pixelMap[(y * w) + i] != -1) {
                return false;
            }
        }
        return true;
    }

    private void findEdges(final int w) {
        boolean isClear = true;

        for(int i = this.downSampleLeft; i <= downSampleRight+1; i++) {
            if(isClear && !vLineClear(i)) {
                isClear = false;
                edges.add(i); //добавили индекс левого конца символа
            } else if(!isClear && vLineClear(i)) {
                isClear = true;
                edges.add(i); //добавили индекс правого конца символа
            }

        }
    }

    protected boolean downSampleRegion(SampleData data, int x, int y) {
        final int w = this.entryImage.getWidth(this);
        final int startX = (int) (data.downSampleLeft + (x * this.ratioX));
        final int startY = (int) (data.downSampleTop + (y * this.ratioY));
        final int endX = (int) (startX + this.ratioX);
        final int endY = (int) (startY + this.ratioY);

        for (int yy = startY; yy <= endY; yy++) {
            for (int xx = startX; xx <= endX; xx++) {
                final int loc = xx + (yy * w);

                if (this.pixelMap[loc] != -1) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void paint(final Graphics g) {
        if (this.entryImage == null) {
            initImage();
        }
        g.drawImage(this.entryImage, 0, 0, this);
        g.setColor(Color.black);
        g.drawRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.red);

        if(this.dataText != null) {
            SampleData data;
            for(int i = 0; i < this.dataText.size(); i++) {
                data = this.dataText.get(i);
                g.drawRect(data.downSampleLeft, data.downSampleTop,
                        data.downSampleRight - data.downSampleLeft,
                        data.downSampleBottom - data.downSampleTop);
            }
        }
    }

    @Override
    public void clear() {
        this.entryGraphics.setColor(Color.white);
        this.entryGraphics.fillRect(0, 0, getWidth(), getHeight());
        this.downSampleBottom = this.downSampleTop = this.downSampleLeft = this.downSampleRight = 0;
        this.edges = null;
        this.dataText = null;
        repaint();
    }
}
