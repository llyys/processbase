/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.dashboard;

import java.util.ArrayList;
import org.jfree.chart.plot.*;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.DefaultCategoryDataset;
import org.processbase.ui.template.ImageSource;

/**
 *
 * @author mgubaidullin
 */
public class Bar3DImageSource extends ImageSource {

    public Bar3DImageSource() {
        super();
        dataset = new DefaultCategoryDataset();
        plot = new CategoryPlot((DefaultCategoryDataset) dataset, new CategoryAxis(null), new NumberAxis(null), new BarRenderer3D());
        chart = new JFreeChart(plot);
//        ((CategoryPlot) plot).setBackgroundPaint(background != null ? background : new Color(230, 235, 239));
        imagebuffer.reset();
    }

    public void setValue(ArrayList<BarValue> values) {
        for (BarValue bv : values) {
            ((DefaultCategoryDataset) dataset).setValue(bv.getValue(), bv.getRowKey(), bv.getColumnKey());
        }
    }
}
