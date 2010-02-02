/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.processbase.ui.template;

import com.vaadin.terminal.StreamResource;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.jfree.data.general.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author mgubaidullin
 */
public class ImageSource implements StreamResource.StreamSource {

    public ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
    public BufferedImage buf = null;
    public AbstractDataset dataset = null;
    public Plot plot = null;
    public JFreeChart chart = null;
    public int reloads = 0;
    public int width = 300;
    public int height = 200;

    public ImageSource() {
        super();
    }

    public ImageSource(int width, int height) {
        super();
        this.width = width;
        this.height = height;
    }

    public InputStream getStream() {
        try {
            buf = chart.createBufferedImage(width, height);
            ImageIO.write(buf, "PNG", imagebuffer);
            return new ByteArrayInputStream(imagebuffer.toByteArray());
        } catch (IOException ex) {
            return null;
        }
    }
}
