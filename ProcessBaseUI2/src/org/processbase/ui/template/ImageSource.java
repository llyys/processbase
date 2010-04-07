/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
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

    protected ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
    protected BufferedImage buf = null;
    protected AbstractDataset dataset = null;
    protected Plot plot = null;
    protected JFreeChart chart = null;
    protected int width;
    protected int height;

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
            ex.printStackTrace();
            return null;
        }
    }
}
