package com.google.zxing.integration.android;

public final class IntentResult
{
  private final String contents;
  private final String errorCorrectionLevel;
  private final String formatName;
  private final Integer orientation;
  private final byte[] rawBytes;

//  IntentResult()
//  {
//  }

  IntentResult(String paramString1, String paramString2, byte[] paramArrayOfByte, Integer paramInteger, String paramString3)
  {
    this.contents = paramString1;
    this.formatName = paramString2;
    this.rawBytes = paramArrayOfByte;
    this.orientation = paramInteger;
    this.errorCorrectionLevel = paramString3;
  }

  public String getContents()
  {
    return this.contents;
  }

  public String getErrorCorrectionLevel()
  {
    return this.errorCorrectionLevel;
  }

  public String getFormatName()
  {
    return this.formatName;
  }

  public Integer getOrientation()
  {
    return this.orientation;
  }

  public byte[] getRawBytes()
  {
    return this.rawBytes;
  }

  public String toString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder(100);
    StringBuilder localStringBuilder2 = localStringBuilder1.append("Format: ");
    String str1 = this.formatName;
    StringBuilder localStringBuilder3 = localStringBuilder2.append(str1).append('\n');
    StringBuilder localStringBuilder4 = localStringBuilder1.append("Contents: ");
    String str2 = this.contents;
    StringBuilder localStringBuilder5 = localStringBuilder4.append(str2).append('\n');
    if (this.rawBytes == null);
    for (int i = 0; ; i = this.rawBytes.length)
    {
      StringBuilder localStringBuilder6 = localStringBuilder1.append("Raw bytes: (").append(i).append(" bytes)\n");
      StringBuilder localStringBuilder7 = localStringBuilder1.append("Orientation: ");
      Integer localInteger = this.orientation;
      StringBuilder localStringBuilder8 = localStringBuilder7.append(localInteger).append('\n');
      StringBuilder localStringBuilder9 = localStringBuilder1.append("EC level: ");
      String str3 = this.errorCorrectionLevel;
      StringBuilder localStringBuilder10 = localStringBuilder9.append(str3).append('\n');
      return localStringBuilder1.toString();
    }
  }
}