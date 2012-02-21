package edu.umich.soar.editor.editors.datamap;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.umich.soar.editor.icons.SoarIcons;
import edu.umich.soar.editor.icons.SoarIcons.IconFiles;

public class SoarDatamapLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object obj, int column) {
		if (obj instanceof DatamapAttribute)
		{
			DatamapAttribute attr = (DatamapAttribute) obj;
			switch (attr.getTarget().type)
			{
			case ENUMERATION:
				return SoarIcons.get(IconFiles.ENUMERATION);
			case FLOAT_RANGE:
				return SoarIcons.get(IconFiles.FLOAT);
			case INT_RANGE:
				return SoarIcons.get(IconFiles.INTEGER);
			case SOAR_ID:
				if (attr.isLinked())
				{
					return SoarIcons.get(IconFiles.LINKED_ATTRIBUTE);
				}
				return SoarIcons.get(IconFiles.ATTRIBUTE);
            case STRING:
                return SoarIcons.get(IconFiles.STRING);
            case LINKED_DATAMAP:
                return SoarIcons.get(IconFiles.A_FLAG);
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object obj, int column) {
		if (obj == null) return "NULL";
		return obj.toString();
	}

}
