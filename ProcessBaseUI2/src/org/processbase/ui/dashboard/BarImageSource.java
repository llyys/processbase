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
package org.processbase.ui.dashboard;

import java.util.ArrayList;
import org.jfree.chart.plot.*;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.processbase.ui.template.ImageSource;

/**
 *
 * @author mgubaidullin
 */
public class BarImageSource extends ImageSource {

    public BarImageSource() {
        super();
        dataset = new DefaultCategoryDataset();
        plot = new CategoryPlot((DefaultCategoryDataset) dataset, new CategoryAxis(null), new NumberAxis(null), new BarRenderer());
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
