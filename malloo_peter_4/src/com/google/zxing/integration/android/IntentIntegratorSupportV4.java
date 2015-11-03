package com.google.zxing.integration.android;

import android.app.Fragment;
import android.content.Intent;
//import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public final class IntentIntegratorSupportV4 extends IntentIntegrator
{
  private final Fragment fragment;

  public IntentIntegratorSupportV4(Fragment paramFragment)
  {
    super(localFragmentActivity);
    this.fragment = paramFragment;
  }

  protected void startActivityForResult(Intent paramIntent, int paramInt)
  {
    this.fragment.startActivityForResult(paramIntent, paramInt);
  }
}