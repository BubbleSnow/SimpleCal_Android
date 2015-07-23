package bubble.SimpleCal;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.bubble.SimpleCal.R;

/**
 * <p>Title: MainActivity</p>
 * <p>Description: Android������</p>
 * @version 3.0.0.150723 
 * @since JDK 1.8.0_45
 * @author bubble
 * @date 2015-7-2
 */
public class MainActivity extends Activity implements OnPageChangeListener{
	final String FILENAME = "history";
	ViewPager viewPager;  
    ArrayList<View> viewList; 
    CalLayout calView;
    HistoryLayout hisView;
    String calHistory;
    String hisHistory;
    boolean isFirstOpenHistory = true;
    boolean isCalHisChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        LayoutInflater inflater = getLayoutInflater();
        calView = new CalLayout(this);
        hisView = new HistoryLayout(this);
        viewList = new ArrayList<View>();  
        viewList.add(calView);
        viewList.add(hisView);
        
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.activity_main, null);  
        viewPager = (ViewPager)viewGroup.findViewById(R.id.viewPager);  
        MyPagerAdapter mAdapter = new MyPagerAdapter(this,viewList);
        viewPager.setAdapter(mAdapter); 
        
        viewPager.addOnPageChangeListener(this);
        setContentView(viewGroup); 
    }
    //������״̬�ı�ʱ����
    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
        
    }

    //����ǰҳ�汻����ʱ����
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * <p>Title: onPageSelected</p>
     * <p>Description: </p>
     * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
     * @param position
     * @author bubble
     * @date 2015-7-19 ����7:03:09
     */
    @Override
    public void onPageSelected(int position) {
    	switch(position){
    	case 0:
    		break;
    	case 1:
    		calHistory = calView.getHistory();
    		if ( isFirstOpenHistory ) {	//��һ�δ򿪣����ر�����ʷ��¼
    			isFirstOpenHistory = false;
    			hisHistory = hisView.load();
    			if ( calHistory != "") {
        			hisView.updateHistory(hisHistory + calHistory);
        			calView.clearCalHistory();
        		}
        		else if ( !TextUtils.isEmpty(hisHistory) ){
        			hisView.updateHistory(hisHistory);
        		}
    		}
    		else if ( calHistory != "") {
    			hisView.updateHistory(calHistory);
    			calView.clearCalHistory();
    		}
    		break;
    	default:
    		break;
    	}
    }
    /* (non-Javadoc)
     * <p>Title: onDestroy</p>
     * <p>Description: </p>
     * @see android.app.Activity#onDestroy()
     * @author bubble
     * @date 2015-7-22 ����09:34:19
     */
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	hisHistory = hisView.getHistory();
    	save(hisHistory);
    	}
    
    /**
     * <p>Title: save</p>
     * <p>Description: </p>
     * @param s
     * @author bubble
     * @date 2015-7-22 ����09:34:01
     */
    public void save(String s){
    	FileOutputStream out = null;
    	BufferedWriter writer = null;
    	try{
    		out = openFileOutput(FILENAME, Context.MODE_PRIVATE);
    		writer = new BufferedWriter(new OutputStreamWriter(out));
    		writer.write(s);
    	} catch (IOException e){
    		e.printStackTrace();
    	} finally {
    		try {
    			if ( writer != null) {
    				writer.flush();
    				writer.close();
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
      
}