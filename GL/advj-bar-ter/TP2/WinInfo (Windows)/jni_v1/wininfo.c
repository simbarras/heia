/*******************************************************************
* wininfo.c
* Getting Windows System Information (Windows 10/11 only)
* 03/2023 R. Scheurer (HEIA-FR)
********************************************************************/
#include <windows.h>
#include <tchar.h>
#include <stdio.h>
#include <strsafe.h>

#include "ShowWinInfo.h"

#define BUFSIZE 256
#define NT_SUCCESS(Status) ((NTSTATUS)(Status) >= 0)

typedef void (WINAPI *PGETSYSTEMINFO)(LPSYSTEM_INFO);
typedef BOOL (WINAPI *PGETPRODUCTINFO)(DWORD, DWORD, DWORD, DWORD, PDWORD);
typedef NTSTATUS (NTAPI *PRTLGETVERSION)(RTL_OSVERSIONINFOEXW *);
void GetRevisionNumber(LPTSTR);

/****************************************************************************
 *  Retrieves information about the operating system
 ****************************************************************************/
//BOOL GetOSInfoString(LPTSTR pszOS)
JNIEXPORT jstring JNICALL Java_ShowWinInfo_getWinInfo(JNIEnv *env, jobject obj)
{
    
  TCHAR wininfo_type[BUFSIZE] = {0};
  TCHAR wininfo_edition[BUFSIZE] = {0};
  TCHAR wininfo_sp[40] = {0};
  TCHAR wininfo_build[40] = {0};
  TCHAR wininfo_arch[10] = {0};
  TCHAR wininfo_rev[10] = {0};

  RTL_OSVERSIONINFOEXW  osvi;
  SYSTEM_INFO si;
  PGETSYSTEMINFO pGetSystemInfo;
  PRTLGETVERSION pRtlGetVersion;
  PGETPRODUCTINFO pGetProductInfo;
  DWORD dwType;
  DWORD dwData;
  DWORD dwSize = sizeof(DWORD)*2;
  HKEY hKey;
  LONG returnStatus;
  NTSTATUS res = -1;

  ZeroMemory(&si, sizeof(SYSTEM_INFO));
  ZeroMemory(&osvi, sizeof(RTL_OSVERSIONINFOEXW));
  osvi.dwOSVersionInfoSize = sizeof(RTL_OSVERSIONINFOEXW);


  //=== Retrieve information ===============================
    
  //--- Get System Info ------------------------------------

  // Call GetNativeSystemInfo if supported or GetSystemInfo otherwise
  pGetSystemInfo = (PGETSYSTEMINFO) GetProcAddress(
    GetModuleHandle(TEXT("kernel32.dll")), 
    "GetNativeSystemInfo");
  if ( pGetSystemInfo != NULL )
    pGetSystemInfo(&si);
  else
    GetSystemInfo(&si);

  //--- Get OS Version ------------------------------------

  // Call RtlGetVersion (instead of deprecated GetVersionEx)
  pRtlGetVersion = (PRTLGETVERSION) GetProcAddress(
    GetModuleHandle(TEXT("ntdll.dll")),
    "RtlGetVersion");
  if (pRtlGetVersion == NULL)
  {
    _tprintf( "\nError : unable to access 'ntdll.dll'\n");
    return FALSE;
  }
  res = pRtlGetVersion(&osvi);
  if (!NT_SUCCESS(res))
  {
    _tprintf( "\nError : unable to use RtlGetVersion()\n");
    return FALSE;
  }

  //--- Get OS Edition -----------------------------------

  pGetProductInfo = (PGETPRODUCTINFO) GetProcAddress(
    GetModuleHandle(TEXT("kernel32.dll")), 
    "GetProductInfo");

  // returns edition info (Home/Pro/Eduction/...) in dwType
  pGetProductInfo( osvi.dwMajorVersion, osvi.dwMinorVersion, 0, 0, &dwType);

  //--- Get Revision Number --------------------------------

  returnStatus = RegOpenKeyEx(HKEY_LOCAL_MACHINE, TEXT("Software\\Microsoft\\Windows NT\\CurrentVersion"),
    0L, KEY_READ, &hKey);

  if (returnStatus  == ERROR_SUCCESS)
  {
    returnStatus = RegQueryValueEx(hKey, TEXT("UBR"), NULL, NULL, (LPBYTE)&dwData, &dwSize);

    if (returnStatus == ERROR_SUCCESS)
      StringCchPrintf(wininfo_rev, 10, TEXT(".%d"), dwData);
  }

  RegCloseKey(hKey);


  //=== Analyse collected information ===============================

  //--- Check for supported OS -----------------------------------
  if ( osvi.dwPlatformId != VER_PLATFORM_WIN32_NT || osvi.dwMajorVersion < 10 )    
  {  
    _tprintf( "\nSorry, can not handle versions before Windows 10\n" );
    return FALSE;
  }

  StringCchCopy(wininfo_type, BUFSIZE, TEXT("Microsoft Windows "));

  //--- Distinguish major OS types -----------------------------------

  if ( osvi.dwMajorVersion == 10 ) // handle Windows 10/11
  {
    if ( osvi.dwBuildNumber >= 22000)
      StringCchCat(wininfo_type, BUFSIZE, TEXT("11"));
    else
      StringCchCat(wininfo_type, BUFSIZE, TEXT("10"));
    
    if ( osvi.dwMinorVersion == 0 )
    {
      if ( osvi.wProductType == VER_NT_WORKSTATION )
      {
                  
        // detect edition       
        switch( dwType )
        {
          case PRODUCT_CORE:
          case PRODUCT_CORE_N:
             StringCchCopy(wininfo_edition, BUFSIZE, TEXT(" Home" ));
             break;
          case PRODUCT_PROFESSIONAL:
          case PRODUCT_PROFESSIONAL_N:
             StringCchCopy(wininfo_edition, BUFSIZE, TEXT(" Pro" ));
             break;
          case PRODUCT_EDUCATION:
          case PRODUCT_EDUCATION_N:
             StringCchCopy(wininfo_edition, BUFSIZE, TEXT(" Education" ));
             break;
          case PRODUCT_ENTERPRISE:
          case PRODUCT_ENTERPRISE_N:
             StringCchCopy(wininfo_edition, BUFSIZE, TEXT(" Enterprise" ));
             break;
          case PRODUCT_MOBILE_CORE:
             StringCchCopy(wininfo_edition, BUFSIZE, TEXT(" Mobile" ));
             break;
          case PRODUCT_MOBILE_ENTERPRISE:
             StringCchCopy(wininfo_edition, BUFSIZE, TEXT(" Mobile Enterprise" ));
             break;
          default:
             StringCchCopy(wininfo_edition, BUFSIZE, TEXT("" ));
        }

        // determine Win11 edition based on build number
        if ( osvi.dwBuildNumber == 22621)
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v22H2"));
        else if ( osvi.dwBuildNumber == 22000)
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v21H2"));
        
        // determine Win10 edition based on build number
        else if ( osvi.dwBuildNumber == 19045)
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v22H2"));
        else if ( osvi.dwBuildNumber == 19043)
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v21H1"));
        else if ( osvi.dwBuildNumber == 19042)
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v20H2"));
        else if ( osvi.dwBuildNumber == 19041)
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v2004"));
        else if ( osvi.dwBuildNumber == 18363 )
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v1909"));
        else if ( osvi.dwBuildNumber == 18362 )
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v1903"));
        else if ( osvi.dwBuildNumber == 17763 )
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v1809"));
        else if ( osvi.dwBuildNumber == 17134 )
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v1803"));
        else if ( osvi.dwBuildNumber == 16299 )
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v1709"));
        else if ( osvi.dwBuildNumber == 15063 )
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v1703"));
        else if ( osvi.dwBuildNumber == 14393 )
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v1607"));
        else if ( osvi.dwBuildNumber == 10586 )
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v1511"));
        else if ( osvi.dwBuildNumber == 10240 ) 
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v1507"));
        else
          StringCchCat(wininfo_edition, BUFSIZE, TEXT(" v?"));
      }
      else StringCchCat(wininfo_edition, BUFSIZE, TEXT("Windows Server 2016" ));
      
    }
    else StringCchCat(wininfo_edition, BUFSIZE, TEXT("?minor?" ));
    
  } // end handling Windows 10
  
  //--- Detect architecture, service pack, and build number -----------------------------------

  // architecture
  if ( osvi.dwMajorVersion >= 6 )
  {
    if ( si.wProcessorArchitecture==PROCESSOR_ARCHITECTURE_AMD64 )
      StringCchCopy(wininfo_arch, 10, TEXT( " (x64)" ));
    else if (si.wProcessorArchitecture==PROCESSOR_ARCHITECTURE_INTEL )
      StringCchCopy(wininfo_arch, 10, TEXT( " (x86)" ));
    else if (si.wProcessorArchitecture==PROCESSOR_ARCHITECTURE_IA64 )
      StringCchCopy(wininfo_arch, 10, TEXT( " (IA64)" ));
    else if (si.wProcessorArchitecture==PROCESSOR_ARCHITECTURE_ARM )
      StringCchCopy(wininfo_arch, 10, TEXT( " (ARM)" ));
    else
      StringCchCopy(wininfo_arch, 30, TEXT( " (unknown architecture)" ));
  }

  // service pack (if any) 
  if ( osvi.wServicePackMajor > 0 )
    StringCchPrintf(wininfo_sp, 40, TEXT(", Service Pack %d"), osvi.wServicePackMajor);

  // build number
  StringCchPrintf(wininfo_build, 40, TEXT(", build %d"), osvi.dwBuildNumber);
    
  //=== Prepare answer string  ===============================
  TCHAR  pszOS[BUFSIZE];

  StringCchPrintf(pszOS, BUFSIZE, TEXT("%s%s%s%s%s%s"),
    wininfo_type, wininfo_edition, wininfo_arch, wininfo_sp, wininfo_build, wininfo_rev);

  return (*env) -> NewStringUTF(env, pszOS);

}


/****************************************************************************
 *  MAIN
 ****************************************************************************/

//int __cdecl _tmain()
//{
//  TCHAR  infoBuf[BUFSIZE];
//  DWORD  bufCharCount = BUFSIZE;
//  TCHAR  szOS[BUFSIZE];
//
//  // Get and display the device name
//  bufCharCount = BUFSIZE;
//  if ( GetComputerName( infoBuf, &bufCharCount ) )
//    _tprintf( TEXT("\nDevice  : %s"), infoBuf );
//
//  // Get and display the user name
//  bufCharCount = BUFSIZE;
//  if ( GetUserName( infoBuf, &bufCharCount ) )
//    _tprintf( TEXT("\nUser    : %s"), infoBuf );
//
//  // Display OS information
//  if ( GetOSInfoString( szOS ) )
//    _tprintf( TEXT("\nOS Info : %s\n\n"), szOS );
//  }
