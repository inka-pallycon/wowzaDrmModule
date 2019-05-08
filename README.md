# Wowza DRM Key Module Sample.

This library is available for DRM encryption with the [Wowza streaming engine](https://www.wowza.com/products/streaming-engine).  
It is available to companies issuing DRM keys supporting the [CPIX](https://dashif.org/docs/CPIX2.1/HTML/Index.html) API.  
The included sample library contains the Class files available for the [PallyCon](https://www.pallycon.com) service, which provides the [CPIX API](https://pallycon.com/docs/en/multidrm/packaging/cpix-api/). 
 
## Supported Features

| HLS FairPlay | HLS AES-128 |Dash Widevine | Dash PlayReady |
|:------|:------|:------|:------|
| :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |

## Setting Wowza Integration Module
### Creating Wowza Application
Install the Wowza Streaming Engine on a server for live streaming.  
Create the Wowza streaming application on the Applications tab after connecting to http://Wowza-server:8088/ on your browser.  
You can choose VOD or Live applications. If you use both methods, you have to create and configure each application separately.

### Setting Libraries
Copy the files in the /lib, /jar folder to the [WOWZA_HOME]/lib/ folder.

## Configuring Wowza Module
Add this library related setting in /[WOWZA_HOME]/conf/[APPLICATION]/Application.xml. Wowza application for Multi DRM (PlayReady, Widevine, FPS) should be separately created and set up as below.  
Please see this page.  
- https://www.wowza.com/docs/how-to-configure-apple-hls-packetization-cupertinostreaming

### Basic Add property 
```xml
<Properties>
    ...
    <!-- add property -->
    <Property>
        <Name>cupertinoEncryptionAPIBased</Name>
        <Value>true</Value>
        <Type>Boolean</Type>
    </Property>
     <Property>
        <Name>RequestUrl</Name>
        <Value>https://issuekeyserverurl</Value>
    </Property>
     <Property>
     <!-- true : live key rotation / false : single key -->
        <Name>KeyRotation</Name>
        <Value>false</Value>
    </Property>
</Properties>
```
### PallyCon Service Add property
```xml
<Properties>
    ...
    <!-- add property -->
    <Property>
        <Name>PallyConEncToken</Name>
        <Value>xxxxxx</Value>
    </Property>
</Properties>
```
### Basic Add Module
```xml
<Modules>
    <Module>
        <Name>CpixDashDrmModule</Name>
        <Descript>Multi DRM Dash CPIX Module</Descript>
        <Class>cpix.wowza.DashCenc</Class>
    </Module>
    <Module>
        <Name>CpixHlsDrmModule</Name>
        <Descript>Multi DRM Hls CPIX Module</Descript>
        <Class>cpix.wowza.HlsFairPlay</Class>
    </Module>
</Modules>
```
### Sample Class
- Path : cpix.wowza
