//package com.kipling.sdk.ln.utils;
//
//import java.io.InputStream;
//
//import org.xmlpull.v1.XmlPullParser;
//
//import android.util.Xml;
//
//public class XMLTool {
//	 // 瑙ｆ瀽inputstream鐨勫唴瀹� 杩斿洖闆嗗悎
//    public static LoginRspResult getLoginResult(InputStream is) throws Exception {
//    	 LoginRspResult rs = new LoginRspResult().failure();
//         XmlPullParser parser = Xml.newPullParser();
//         parser.setInput(is, "UTF-8");
//
//         int type = parser.getEventType();
//         while (type != XmlPullParser.END_DOCUMENT) {
//              switch (type) {
//              case XmlPullParser.START_TAG:
//                   if ("IdentityInfo".equals(parser.getName())) {
//                	   	rs.success();
//                   } else if ("AccountID".equals(parser.getName())) {
//                        rs.setAccountID(parser.nextText());
//                   } else if ("Username".equals(parser.getName())) {
//                        rs.setUsername(parser.nextText());
//                   } else if ("DeviceID".equals(parser.getName())) {
//                        rs.setDeviceID(parser.nextText());
//                   } else if ("time".equals(parser.getName())) {
//                	    rs.setVerified(parser.nextText());
//                   } else if ("Error".equals(parser.getName())) {
//                	   rs.failure();
//                   } else if ("Code".equals(parser.getName())) {
//                       rs.setErrCode(parser.nextText());
//                   } else if ("Timestamp".equals(parser.getName())) {
//                	   rs.setTimestamp(parser.nextText());
//                   }
//                   break;
//              case XmlPullParser.END_TAG:
////                   if ("IdentityInfo".equals(parser.getName())) {
////                   }else if("Error".equals(parser.getName())){}
//                   break;
//              }// switch
//              type = parser.next();
//         }// while
//         return rs;
//    }
//}
