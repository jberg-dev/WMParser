package coffee.berg.wmparser.Generics;

import javafx.scene.control.CheckBoxTreeItem;

/**
 * Created by Bergerking on 2018-12-28.
 */
public class CustomTreeNode extends CheckBoxTreeItem
{
	private Pair<String, Integer> data;

	public CustomTreeNode(Pair<String, Integer> _data)
	{
		super(_data.toString());
		data = _data;
	}

	public Pair<String, Integer> getData()
	{
		return data;
	}


}
