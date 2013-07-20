package app.ennovia.compass;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

public class CompassActivity extends Activity {
	static final private int MENU_ITEM = Menu.FIRST;
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		CompassView cv = new CompassView(this);
		setContentView(cv);
		cv.setBearing(90);
		//registerForContextMenu(cv);
	}
	static final private int ADD_NEW = Menu.FIRST;
	static final private int REMOVE = Menu.FIRST + 1;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	super.onCreateOptionsMenu(menu);
	// Create and add new menu items.
	MenuItem itemAdd = menu.add(0, ADD_NEW, Menu.NONE,	R.string.add_new);
	MenuItem itemRem = menu.add(0, REMOVE, Menu.NONE,	R.string.remove);
	// Assign icons
	itemAdd.setIcon(R.drawable.ic_launcher);
	itemRem.setIcon(R.drawable.ic_launcher);
	itemAdd.setShortcut('0', 'a');
	itemRem.setShortcut('1', 'r');
	return true;
	
	
	}

}
