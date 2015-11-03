package com.google.zxing.integration.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class IntentIntegrator
{
  public static final Collection<String> ALL_CODE_TYPES;
  private static final String BSPLUS_PACKAGE = "com.srowen.bs.android";
  private static final String BS_PACKAGE = "com.google.zxing.client.android";
  public static final Collection<String> DATA_MATRIX_TYPES;
  public static final String DEFAULT_MESSAGE = "This application requires Barcode Scanner. Would you like to install it?";
  public static final String DEFAULT_NO = "No";
  public static final String DEFAULT_TITLE = "Install Barcode Scanner?";
  public static final String DEFAULT_YES = "Yes";
  public static final Collection<String> ONE_D_CODE_TYPES;
  public static final Collection<String> PRODUCT_CODE_TYPES;
  public static final Collection<String> QR_CODE_TYPES;
  public static final int REQUEST_CODE = 49374;
  private static final String TAG = IntentIntegrator.class.getSimpleName();
  public static final Collection<String> TARGET_ALL_KNOWN;
  public static final Collection<String> TARGET_BARCODE_SCANNER_ONLY;
  private final Activity activity;
  private String buttonNo;
  private String buttonYes;
  private String message;
  private final Map<String, Object> moreExtras;
  private Collection<String> targetApplications;
  private String title;

  static
  {
    String[] arrayOfString1 = new String[5];
    arrayOfString1[0] = "UPC_A";
    arrayOfString1[1] = "UPC_E";
    arrayOfString1[2] = "EAN_8";
    arrayOfString1[3] = "EAN_13";
    arrayOfString1[4] = "RSS_14";
    PRODUCT_CODE_TYPES = list(arrayOfString1);
    String[] arrayOfString2 = new String[10];
    arrayOfString2[0] = "UPC_A";
    arrayOfString2[1] = "UPC_E";
    arrayOfString2[2] = "EAN_8";
    arrayOfString2[3] = "EAN_13";
    arrayOfString2[4] = "CODE_39";
    arrayOfString2[5] = "CODE_93";
    arrayOfString2[6] = "CODE_128";
    arrayOfString2[7] = "ITF";
    arrayOfString2[8] = "RSS_14";
    arrayOfString2[9] = "RSS_EXPANDED";
    ONE_D_CODE_TYPES = list(arrayOfString2);
    QR_CODE_TYPES = Collections.singleton("QR_CODE");
    DATA_MATRIX_TYPES = Collections.singleton("DATA_MATRIX");
    ALL_CODE_TYPES = null;
    TARGET_BARCODE_SCANNER_ONLY = Collections.singleton("com.google.zxing.client.android");
    String[] arrayOfString3 = new String[3];
    arrayOfString3[0] = "com.google.zxing.client.android";
    arrayOfString3[1] = "com.srowen.bs.android";
    arrayOfString3[2] = "com.srowen.bs.android.simple";
    TARGET_ALL_KNOWN = list(arrayOfString3);
  }

  public IntentIntegrator(Activity paramActivity)
  {
    this.activity = paramActivity;
    this.title = "Install Barcode Scanner?";
    this.message = "This application requires Barcode Scanner. Would you like to install it?";
    this.buttonYes = "Yes";
    this.buttonNo = "No";
    Collection localCollection = TARGET_ALL_KNOWN;
    this.targetApplications = localCollection;
    HashMap localHashMap = new HashMap(3);
    this.moreExtras = localHashMap;
  }

  private void attachMoreExtras(Intent paramIntent)
  {
    Iterator localIterator = this.moreExtras.entrySet().iterator();
    while (true)
    {
      if (!localIterator.hasNext())
        return;
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1 = (String)localEntry.getKey();
      Object localObject = localEntry.getValue();
      if ((localObject instanceof Integer))
      {
        Integer localInteger = (Integer)localObject;
        Intent localIntent1 = paramIntent.putExtra(str1, localInteger);
        continue;
      }
      if ((localObject instanceof Long))
      {
        Long localLong = (Long)localObject;
        Intent localIntent2 = paramIntent.putExtra(str1, localLong);
        continue;
      }
      if ((localObject instanceof Boolean))
      {
        Boolean localBoolean = (Boolean)localObject;
        Intent localIntent3 = paramIntent.putExtra(str1, localBoolean);
        continue;
      }
      if ((localObject instanceof Double))
      {
        Double localDouble = (Double)localObject;
        Intent localIntent4 = paramIntent.putExtra(str1, localDouble);
        continue;
      }
      if ((localObject instanceof Float))
      {
        Float localFloat = (Float)localObject;
        Intent localIntent5 = paramIntent.putExtra(str1, localFloat);
        continue;
      }
      if ((localObject instanceof Bundle))
      {
        Bundle localBundle = (Bundle)localObject;
        Intent localIntent6 = paramIntent.putExtra(str1, localBundle);
        continue;
      }
      String str2 = localObject.toString();
      Intent localIntent7 = paramIntent.putExtra(str1, str2);
    }
  }

  private String findTargetAppPackage(Intent paramIntent)
  {
    List localList = this.activity.getPackageManager().queryIntentActivities(paramIntent, 65536);
    String str;
    if (localList != null)
    {
      Iterator localIterator = localList.iterator();
      do
      {
        if (!localIterator.hasNext())
          break;
        str = ((ResolveInfo)localIterator.next()).activityInfo.packageName;
      }
      while (!this.targetApplications.contains(str));
    }
    while (true)
    {
      return str;
      str = null;
    }
  }

  private static Collection<String> list(String[] paramArrayOfString)
  {
    return Collections.unmodifiableCollection(Arrays.asList(paramArrayOfString));
  }

  public static IntentResult parseActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    Integer localInteger = null;
    int i;
    IntentResult localIntentResult;
    if (paramInt1 == 49374)
      if (paramInt2 == -1)
      {
        String str1 = paramIntent.getStringExtra("SCAN_RESULT");
        String str2 = paramIntent.getStringExtra("SCAN_RESULT_FORMAT");
        byte[] arrayOfByte = paramIntent.getByteArrayExtra("SCAN_RESULT_BYTES");
        i = paramIntent.getIntExtra("SCAN_RESULT_ORIENTATION", -2147483648);
        if (i == -2147483648)
        {
          String str3 = paramIntent.getStringExtra("SCAN_RESULT_ERROR_CORRECTION_LEVEL");
          localIntentResult = new IntentResult(str1, str2, arrayOfByte, localInteger, str3);
        }
      }
    while (true)
    {
      return localIntentResult;
      localInteger = Integer.valueOf(i);
      break;
      localIntentResult = new IntentResult();
      continue;
      localIntentResult = null;
    }
  }

  private AlertDialog showDownloadDialog()
  {
    Activity localActivity = this.activity;
    AlertDialog.Builder localBuilder1 = new AlertDialog.Builder(localActivity);
    String str1 = this.title;
    AlertDialog.Builder localBuilder2 = localBuilder1.setTitle(str1);
    String str2 = this.message;
    AlertDialog.Builder localBuilder3 = localBuilder1.setMessage(str2);
    String str3 = this.buttonYes;
    1 local1 = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        Uri localUri = Uri.parse("market://details?id=com.google.zxing.client.android");
        Intent localIntent = new Intent("android.intent.action.VIEW", localUri);
        try
        {
          IntentIntegrator.this.activity.startActivity(localIntent);
          return;
        }
        catch (ActivityNotFoundException localActivityNotFoundException)
        {
          int i = Log.w(IntentIntegrator.TAG, "Android Market is not installed; cannot install Barcode Scanner");
        }
      }
    };
    AlertDialog.Builder localBuilder4 = localBuilder1.setPositiveButton(str3, local1);
    String str4 = this.buttonNo;
    2 local2 = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
      }
    };
    AlertDialog.Builder localBuilder5 = localBuilder1.setNegativeButton(str4, local2);
    return localBuilder1.show();
  }

  public void addExtra(String paramString, Object paramObject)
  {
    Object localObject = this.moreExtras.put(paramString, paramObject);
  }

  public String getButtonNo()
  {
    return this.buttonNo;
  }

  public String getButtonYes()
  {
    return this.buttonYes;
  }

  public String getMessage()
  {
    return this.message;
  }

  public Map<String, ?> getMoreExtras()
  {
    return this.moreExtras;
  }

  public Collection<String> getTargetApplications()
  {
    return this.targetApplications;
  }

  public String getTitle()
  {
    return this.title;
  }

  public AlertDialog initiateScan()
  {
    Collection localCollection = ALL_CODE_TYPES;
    return initiateScan(localCollection);
  }

  public AlertDialog initiateScan(Collection<String> paramCollection)
  {
    Intent localIntent1 = new Intent("com.google.zxing.client.android.SCAN");
    Intent localIntent2 = localIntent1.addCategory("android.intent.category.DEFAULT");
    if (paramCollection != null)
    {
      StringBuilder localStringBuilder1 = new StringBuilder();
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        String str1 = (String)localIterator.next();
        if (localStringBuilder1.length() > 0)
          StringBuilder localStringBuilder2 = localStringBuilder1.append(',');
        StringBuilder localStringBuilder3 = localStringBuilder1.append(str1);
      }
      String str2 = localStringBuilder1.toString();
      Intent localIntent3 = localIntent1.putExtra("SCAN_FORMATS", str2);
    }
    String str3 = findTargetAppPackage(localIntent1);
    if (str3 == null);
    for (AlertDialog localAlertDialog = showDownloadDialog(); ; localAlertDialog = null)
    {
      return localAlertDialog;
      Intent localIntent4 = localIntent1.setPackage(str3);
      Intent localIntent5 = localIntent1.addFlags(67108864);
      Intent localIntent6 = localIntent1.addFlags(524288);
      attachMoreExtras(localIntent1);
      startActivityForResult(localIntent1, 49374);
    }
  }

  public void setButtonNo(String paramString)
  {
    this.buttonNo = paramString;
  }

  public void setButtonNoByID(int paramInt)
  {
    String str = this.activity.getString(paramInt);
    this.buttonNo = str;
  }

  public void setButtonYes(String paramString)
  {
    this.buttonYes = paramString;
  }

  public void setButtonYesByID(int paramInt)
  {
    String str = this.activity.getString(paramInt);
    this.buttonYes = str;
  }

  public void setMessage(String paramString)
  {
    this.message = paramString;
  }

  public void setMessageByID(int paramInt)
  {
    String str = this.activity.getString(paramInt);
    this.message = str;
  }

  public void setSingleTargetApplication(String paramString)
  {
    Set localSet = Collections.singleton(paramString);
    this.targetApplications = localSet;
  }

  public void setTargetApplications(Collection<String> paramCollection)
  {
    this.targetApplications = paramCollection;
  }

  public void setTitle(String paramString)
  {
    this.title = paramString;
  }

  public void setTitleByID(int paramInt)
  {
    String str = this.activity.getString(paramInt);
    this.title = str;
  }

  public AlertDialog shareText(CharSequence paramCharSequence)
  {
    return shareText(paramCharSequence, "TEXT_TYPE");
  }

  public AlertDialog shareText(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    Intent localIntent1 = new Intent();
    Intent localIntent2 = localIntent1.addCategory("android.intent.category.DEFAULT");
    Intent localIntent3 = localIntent1.setAction("com.google.zxing.client.android.ENCODE");
    Intent localIntent4 = localIntent1.putExtra("ENCODE_TYPE", paramCharSequence2);
    Intent localIntent5 = localIntent1.putExtra("ENCODE_DATA", paramCharSequence1);
    String str = findTargetAppPackage(localIntent1);
    if (str == null);
    for (AlertDialog localAlertDialog = showDownloadDialog(); ; localAlertDialog = null)
    {
      return localAlertDialog;
      Intent localIntent6 = localIntent1.setPackage(str);
      Intent localIntent7 = localIntent1.addFlags(67108864);
      Intent localIntent8 = localIntent1.addFlags(524288);
      attachMoreExtras(localIntent1);
      this.activity.startActivity(localIntent1);
    }
  }

  protected void startActivityForResult(Intent paramIntent, int paramInt)
  {
    this.activity.startActivityForResult(paramIntent, paramInt);
  }
}