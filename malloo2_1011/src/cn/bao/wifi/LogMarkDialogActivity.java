package cn.bao.wifi;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * ��task���������ʹ��
 * 
 * @author Administrator
 * 
 */
public class LogMarkDialogActivity extends Activity {

	PackageManager pm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_mark_activity);

		TextView tv_content = (TextView) this.findViewById(R.id.tv_content);
		
	    //��ȡ���ǰ�������ͼ
        Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		String s = bundle.getString("log");
		tv_content.setText(s);

	} 
	public void close(View view) {
		finish();
	}
	// ������Ļ
	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// // �ᴥ��Ļ
	// if (event.getAction() == MotionEvent.ACTION_DOWN) {
	//
	// finish();
	// }
	// return super.onTouchEvent(event);
	// }

}
