(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-27659703"],{"2c5b":function(e,t,a){"use strict";var s=function(){var e=this,t=this,a=t.$createElement,s=t._self._c||a;return s("a-drawer",{attrs:{title:"页面设置",width:t.isMobile()?"100%":"460",placement:"right",closable:"",destroyOnClose:"",visible:t.visible},on:{close:t.onClose}},[s("a-skeleton",{attrs:{active:"",loading:t.settingLoading,paragraph:{rows:18}}},[s("div",{staticClass:"post-setting-drawer-content"},[s("div",{style:{marginBottom:"16px"}},[s("h3",{staticClass:"post-setting-drawer-title"},[t._v("基本设置")]),s("div",{staticClass:"post-setting-drawer-item"},[s("a-form",{attrs:{layout:"vertical"}},[t.needTitle?s("a-form-item",{attrs:{label:"页面标题："}},[s("a-input",{model:{value:t.selectedSheet.title,callback:function(e){t.$set(t.selectedSheet,"title",e)},expression:"selectedSheet.title"}})],1):t._e(),s("a-form-item",{attrs:{label:"页面路径：",help:t.options.blog_url+"/s/"+(t.selectedSheet.url?t.selectedSheet.url:"{auto_generate}")}},[s("a-input",{model:{value:t.selectedSheet.url,callback:function(e){t.$set(t.selectedSheet,"url",e)},expression:"selectedSheet.url"}})],1),s("a-form-item",{attrs:{label:"发表时间："}},[s("a-date-picker",{attrs:{showTime:"",defaultValue:t.pickerDefaultValue,format:"YYYY-MM-DD HH:mm:ss",placeholder:"选择页面发表时间"},on:{change:t.onSheetDateChange,ok:t.onSheetDateOk}})],1),s("a-form-item",{attrs:{label:"开启评论："}},[s("a-radio-group",{attrs:{defaultValue:!1},model:{value:t.selectedSheet.disallowComment,callback:function(e){t.$set(t.selectedSheet,"disallowComment",e)},expression:"selectedSheet.disallowComment"}},[s("a-radio",{attrs:{value:!1}},[t._v("开启")]),s("a-radio",{attrs:{value:!0}},[t._v("关闭")])],1)],1),s("a-form-item",{attrs:{label:"自定义模板："}},[s("a-select",{model:{value:t.selectedSheet.template,callback:function(e){t.$set(t.selectedSheet,"template",e)},expression:"selectedSheet.template"}},[s("a-select-option",{key:"",attrs:{value:""}},[t._v("无")]),t._l(t.customTpls,(function(e){return s("a-select-option",{key:e,attrs:{value:e}},[t._v(t._s(e))])}))],2)],1)],1)],1)]),s("a-divider"),s("div",{style:{marginBottom:"16px"}},[s("h3",{staticClass:"post-setting-drawer-title"},[t._v("缩略图")]),s("div",{staticClass:"post-setting-drawer-item"},[s("div",{staticClass:"sheet-thumb"},[s("img",{staticClass:"img",attrs:{src:t.selectedSheet.thumbnail||"/images/placeholder.jpg"},on:{click:function(){return e.thumbDrawerVisible=!0}}}),s("a-button",{staticClass:"sheet-thumb-remove",attrs:{type:"dashed"},on:{click:t.handlerRemoveThumb}},[t._v("移除")])],1)])]),s("a-divider"),s("div",{style:{marginBottom:"16px"}},[s("h3",{staticClass:"post-setting-drawer-title"},[t._v("元数据")]),s("a-form",{attrs:{layout:"vertical"}},[t._l(t.selectedSheetMetas,(function(e,a){return s("a-form-item",{key:a,attrs:{prop:"sheetMeta."+a+".value"}},[s("a-row",{attrs:{gutter:5}},[s("a-col",{attrs:{span:12}},[s("a-input",{model:{value:e.key,callback:function(a){t.$set(e,"key",a)},expression:"sheetMeta.key"}},[s("i",{attrs:{slot:"addonBefore"},slot:"addonBefore"},[t._v("K")])])],1),s("a-col",{attrs:{span:12}},[s("a-input",{model:{value:e.value,callback:function(a){t.$set(e,"value",a)},expression:"sheetMeta.value"}},[s("i",{attrs:{slot:"addonBefore"},slot:"addonBefore"},[t._v("V")]),s("a",{attrs:{slot:"addonAfter",href:"javascript:void(0);"},on:{click:function(a){return a.preventDefault(),t.handleRemoveSheetMeta(e)}},slot:"addonAfter"},[s("a-icon",{attrs:{type:"close"}})],1)])],1)],1)],1)})),s("a-form-item",[s("a-button",{attrs:{type:"dashed"},on:{click:function(e){return t.handleInsertSheetMeta()}}},[t._v("新增")])],1)],2)],1),s("a-divider",{staticClass:"divider-transparent"})],1)]),s("AttachmentSelectDrawer",{attrs:{drawerWidth:460},on:{listenToSelect:t.handleSelectSheetThumb},model:{value:t.thumbDrawerVisible,callback:function(e){t.thumbDrawerVisible=e},expression:"thumbDrawerVisible"}}),s("div",{staticClass:"bottom-control"},[s("a-button",{staticStyle:{marginRight:"8px"},on:{click:t.handleDraftClick}},[t._v("保存草稿")]),s("a-button",{attrs:{type:"primary"},on:{click:t.handlePublishClick}},[t._v("发布")])],1)],1)},n=[],i=(a("8e6e"),a("ac6a"),a("456d"),a("bd86")),o=a("ac0d"),r=a("c1df"),l=a.n(r),c=a("3993"),h=a("5880"),d=a("12de"),u=a("ed66");function f(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);t&&(s=s.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,s)}return a}function m(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?f(Object(a),!0).forEach((function(t){Object(i["a"])(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):f(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}var S={name:"SheetSettingDrawer",mixins:[o["a"],o["b"]],components:{AttachmentSelectDrawer:c["a"]},data:function(){return{thumbDrawerVisible:!1,settingLoading:!0,selectedSheet:this.sheet,customTpls:[]}},props:{sheet:{type:Object,required:!0},sheetMetas:{type:Array,required:!0},needTitle:{type:Boolean,required:!1,default:!1},visible:{type:Boolean,required:!1,default:!0}},created:function(){this.loadSkeleton(),this.loadCustomTpls()},watch:{sheet:function(e){this.selectedSheet=e},selectedSheet:function(e){this.$emit("onRefreshSheet",e)},selectedSheetMetas:function(e){this.$emit("onRefreshSheetMetas",e)},visible:function(e,t){e&&(this.loadSkeleton(),this.loadPresetMetasField())}},computed:m({selectedSheetMetas:function(){return this.sheetMetas},pickerDefaultValue:function(){if(this.selectedSheet.createTime){var e=new Date(this.selectedSheet.createTime);return l()(e,"YYYY-MM-DD HH:mm:ss")}return l()(new Date,"YYYY-MM-DD HH:mm:ss")}},Object(h["mapGetters"])(["options"])),methods:{loadSkeleton:function(){var e=this;this.settingLoading=!0,setTimeout((function(){e.settingLoading=!1}),500)},loadPresetMetasField:function(){var e=this;this.sheetMetas.length<=0&&d["a"].getActivatedTheme().then((function(t){var a=t.data.data.sheetMetaField;if(a&&a.length>0)for(var s=0,n=a.length;s<n;s++)e.selectedSheetMetas.push({value:"",key:a[s]})}))},loadCustomTpls:function(){var e=this;d["a"].customSheetTpls().then((function(t){e.customTpls=t.data.data}))},handleSelectSheetThumb:function(e){this.selectedSheet.thumbnail=encodeURI(e.path),this.thumbDrawerVisible=!1},handlerRemoveThumb:function(){this.selectedSheet.thumbnail=null},handlePublishClick:function(){this.selectedSheet.status="PUBLISHED",this.saveSheet()},handleDraftClick:function(){this.selectedSheet.status="DRAFT",this.saveSheet()},saveSheet:function(){var e=this;this.createOrUpdateSheet((function(){return e.$message.success("页面发布成功！")}),(function(){return e.$message.success("页面发布成功！")}),!1)},createOrUpdateSheet:function(e,t,a){var s=this;this.selectedSheet.title?(this.selectedSheet.sheetMetas=this.selectedSheetMetas,this.selectedSheet.id?u["a"].update(this.selectedSheet.id,this.selectedSheet,a).then((function(e){s.$log.debug("Updated sheet",e.data.data),t&&(t(),s.$emit("onSaved",!0),s.$router.push({name:"SheetList"}))})):u["a"].create(this.selectedSheet,a).then((function(t){s.$log.debug("Created sheet",t.data.data),e&&(e(),s.$emit("onSaved",!0),s.$router.push({name:"SheetList"})),s.selectedSheet=t.data.data}))):this.$notification["error"]({message:"提示",description:"页面标题不能为空！"})},onClose:function(){this.$emit("close",!1)},onSheetDateChange:function(e,t){this.selectedSheet.createTime=e.valueOf()},onSheetDateOk:function(e){this.selectedSheet.createTime=e.valueOf()},handleRemoveSheetMeta:function(e){var t=this.selectedSheetMetas.indexOf(e);-1!==t&&this.selectedSheetMetas.splice(t,1)},handleInsertSheetMeta:function(){this.selectedSheetMetas.push({value:"",key:""})}}},p=S,g=a("2877"),b=Object(g["a"])(p,s,n,!1,null,null,null);t["a"]=b.exports},ed66:function(e,t,a){"use strict";var s=a("9efd"),n="/api/admin/sheets",i={list:function(e){return Object(s["a"])({url:n,params:e,method:"get"})},listInternal:function(){return Object(s["a"])({url:"".concat(n,"/internal"),method:"get"})},get:function(e){return Object(s["a"])({url:"".concat(n,"/").concat(e),method:"get"})},create:function(e,t){return Object(s["a"])({url:n,method:"post",data:e,params:{autoSave:t}})},update:function(e,t,a){return Object(s["a"])({url:"".concat(n,"/").concat(e),method:"put",data:t,params:{autoSave:a}})},updateStatus:function(e,t){return Object(s["a"])({url:"".concat(n,"/").concat(e,"/").concat(t),method:"put"})},delete:function(e){return Object(s["a"])({url:"".concat(n,"/").concat(e),method:"delete"})},preview:function(e){return Object(s["a"])({url:"".concat(n,"/preview/").concat(e),method:"get"})},sheetStatus:{PUBLISHED:{color:"green",status:"success",text:"已发布"},DRAFT:{color:"yellow",status:"warning",text:"草稿"},RECYCLE:{color:"red",status:"error",text:"回收站"}}};t["a"]=i},f585:function(e,t,a){"use strict";a.r(t);var s=function(){var e=this,t=this,a=t.$createElement,s=t._self._c||a;return s("div",[s("a-row",{attrs:{gutter:12}},[s("a-col",{attrs:{span:24}},[s("div",{staticStyle:{"margin-bottom":"16px"}},[s("a-input",{attrs:{size:"large",placeholder:"请输入页面标题"},model:{value:t.sheetToStage.title,callback:function(e){t.$set(t.sheetToStage,"title",e)},expression:"sheetToStage.title"}})],1),s("div",{attrs:{id:"editor"}},[s("halo-editor",{ref:"md",attrs:{boxShadow:!1,toolbars:t.toolbars,ishljs:!0,autofocus:!1},on:{imgAdd:t.handleAttachmentUpload,save:t.handleSaveDraft},model:{value:t.sheetToStage.originalContent,callback:function(e){t.$set(t.sheetToStage,"originalContent",e)},expression:"sheetToStage.originalContent"}})],1)])],1),s("SheetSettingDrawer",{attrs:{sheet:t.sheetToStage,sheetMetas:t.selectedSheetMetas,visible:t.sheetSettingVisible},on:{close:t.onSheetSettingsClose,onRefreshSheet:t.onRefreshSheetFromSetting,onRefreshSheetMetas:t.onRefreshSheetMetasFromSetting,onSaved:t.onSaved}}),s("AttachmentDrawer",{model:{value:t.attachmentDrawerVisible,callback:function(e){t.attachmentDrawerVisible=e},expression:"attachmentDrawerVisible"}}),s("footer-tool-bar",{style:{width:t.isSideMenu()&&t.isDesktop()?"calc(100% - "+(t.sidebarOpened?256:80)+"px)":"100%"}},[s("a-button",{attrs:{type:"danger"},on:{click:t.handleSaveDraft}},[t._v("保存草稿")]),s("a-button",{staticStyle:{"margin-left":"8px"},on:{click:t.handlePreview}},[t._v("预览")]),s("a-button",{staticStyle:{"margin-left":"8px"},attrs:{type:"primary"},on:{click:t.handleShowSheetSetting}},[t._v("发布")]),s("a-button",{staticStyle:{"margin-left":"8px"},attrs:{type:"dashed"},on:{click:function(){return e.attachmentDrawerVisible=!0}}},[t._v("附件库")])],1)],1)},n=[],i=(a("8e6e"),a("ac6a"),a("456d"),a("bd86")),o=a("ac0d"),r=a("5880"),l=a("c1df"),c=a.n(l),h=a("2749"),d=a("2c5b"),u=a("ed4e"),f=a("5a70"),m=a("6ea2"),S=(a("6648"),a("ed66")),p=a("a796");function g(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);t&&(s=s.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,s)}return a}function b(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?g(Object(a),!0).forEach((function(t){Object(i["a"])(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):g(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}var v={components:{haloEditor:m["haloEditor"],FooterToolBar:f["a"],AttachmentDrawer:u["a"],SheetSettingDrawer:d["a"]},mixins:[o["a"],o["b"]],data:function(){return{toolbars:h["a"],attachmentDrawerVisible:!1,sheetSettingVisible:!1,sheetToStage:{},selectedSheetMetas:[],isSaved:!1,contentChanges:0}},beforeRouteEnter:function(e,t,a){var s=e.query.sheetId;a((function(e){s&&S["a"].get(s).then((function(t){var a=t.data.data;e.sheetToStage=a,e.selectedSheetMetas=a.sheetMetas}))}))},destroyed:function(){this.sheetSettingVisible&&(this.sheetSettingVisible=!1),this.attachmentDrawerVisible&&(this.attachmentDrawerVisible=!1),window.onbeforeunload&&(window.onbeforeunload=null)},beforeRouteLeave:function(e,t,a){this.$createElement;this.sheetSettingVisible&&(this.sheetSettingVisible=!1),this.attachmentDrawerVisible&&(this.attachmentDrawerVisible=!1),this.contentChanges<=1?a():this.isSaved?a():this.$confirm({title:"当前页面数据未保存，确定要离开吗？",content:function(e){return e("div",{style:"color:red;"},["如果离开当面页面，你的数据很可能会丢失！"])},onOk:function(){a()},onCancel:function(){a(!1)}})},mounted:function(){window.onbeforeunload=function(e){return e=e||window.event,e&&(e.returnValue="当前页面数据未保存，确定要离开吗？"),"当前页面数据未保存，确定要离开吗？"}},watch:{temporaryContent:function(e,t){e&&this.contentChanges++}},computed:b({temporaryContent:function(){return this.sheetToStage.originalContent}},Object(r["mapGetters"])(["options"])),methods:{handleSaveDraft:function(){var e=this;this.sheetToStage.status="DRAFT",this.sheetToStage.title||(this.sheetToStage.title=c()(new Date).format("YYYY-MM-DD-HH-mm-ss")),this.sheetToStage.id?S["a"].update(this.sheetToStage.id,this.sheetToStage,!1).then((function(t){e.$log.debug("Updated sheet",t.data.data),e.$message.success("保存草稿成功！")})):S["a"].create(this.sheetToStage,!1).then((function(t){e.$log.debug("Created sheet",t.data.data),e.$message.success("保存草稿成功！"),e.sheetToStage=t.data.data}))},handleAttachmentUpload:function(e,t){var a=this,s=new FormData;s.append("file",t),p["a"].upload(s).then((function(t){var s=t.data;if(200===s.status){var n=a.$refs.md;n.$img2Url(e,encodeURI(s.data.path)),a.$message.success("图片上传成功！")}else a.$message.error("图片上传失败："+s.message)}))},handleShowSheetSetting:function(){this.sheetSettingVisible=!0},handlePreview:function(){var e=this;this.sheetToStage.status="DRAFT",this.sheetToStage.title||(this.sheetToStage.title=c()(new Date).format("YYYY-MM-DD-HH-mm-ss")),this.sheetToStage.originalContent||(this.sheetToStage.originalContent="开始编辑..."),this.sheetToStage.id?S["a"].update(this.sheetToStage.id,this.sheetToStage,!1).then((function(t){e.$log.debug("Updated sheet",t.data.data),S["a"].preview(e.sheetToStage.id).then((function(e){window.open(e.data,"_blank")}))})):S["a"].create(this.sheetToStage,!1).then((function(t){e.$log.debug("Created sheet",t.data.data),e.sheetToStage=t.data.data,S["a"].preview(e.sheetToStage.id).then((function(e){window.open(e.data,"_blank")}))}))},onSheetSettingsClose:function(){this.sheetSettingVisible=!1},onRefreshSheetFromSetting:function(e){this.sheetToStage=e},onRefreshSheetMetasFromSetting:function(e){this.selectedSheetMetas=e},onSaved:function(e){this.isSaved=e}}},w=v,D=a("2877"),y=Object(D["a"])(w,s,n,!1,null,null,null);t["default"]=y.exports}}]);