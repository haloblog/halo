(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-2d21a35c"],{bb17:function(t,e,n){"use strict";n.r(e);var a=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"page-header-index-wide"},[n("a-row",{attrs:{gutter:12}},[n("a-col",{style:{"padding-bottom":"12px"},attrs:{xl:10,lg:10,md:10,sm:24,xs:24}},[n("a-card",{attrs:{title:t.title,bodyStyle:{padding:"16px"}}},[n("a-form",{attrs:{layout:"horizontal"}},[n("a-form-item",{attrs:{label:"网站名称："}},[n("a-input",{model:{value:t.link.name,callback:function(e){t.$set(t.link,"name",e)},expression:"link.name"}})],1),n("a-form-item",{attrs:{label:"网站地址：",help:"* 需要加上 http://"}},[n("a-input",{model:{value:t.link.url,callback:function(e){t.$set(t.link,"url",e)},expression:"link.url"}})],1),n("a-form-item",{attrs:{label:"Logo："}},[n("a-input",{model:{value:t.link.logo,callback:function(e){t.$set(t.link,"logo",e)},expression:"link.logo"}})],1),n("a-form-item",{attrs:{label:"分组：",help:"* 非必填"}},[n("a-input",{model:{value:t.link.team,callback:function(e){t.$set(t.link,"team",e)},expression:"link.team"}})],1),n("a-form-item",{attrs:{label:"描述："}},[n("a-input",{attrs:{type:"textarea",autosize:{minRows:5}},model:{value:t.link.description,callback:function(e){t.$set(t.link,"description",e)},expression:"link.description"}})],1),n("a-form-item",["create"===t.formType?n("a-button",{attrs:{type:"primary"},on:{click:t.handleSaveClick}},[t._v("保存")]):n("a-button-group",[n("a-button",{attrs:{type:"primary"},on:{click:t.handleSaveClick}},[t._v("更新")]),"update"===t.formType?n("a-button",{attrs:{type:"dashed"},on:{click:t.handleAddLink}},[t._v("返回添加")]):t._e()],1)],1)],1)],1)],1),n("a-col",{style:{"padding-bottom":"12px"},attrs:{xl:14,lg:14,md:14,sm:24,xs:24}},[n("a-card",{attrs:{title:"所有友情链接",bodyStyle:{padding:"16px"}}},[n("a-table",{attrs:{columns:t.columns,dataSource:t.links,loading:t.loading,rowKey:function(t){return t.id}},scopedSlots:t._u([{key:"url",fn:function(e){return[n("a",{attrs:{target:"_blank",href:e}},[t._v(t._s(e))])]}},{key:"name",fn:function(e){return n("ellipsis",{attrs:{length:15,tooltip:""}},[t._v(t._s(e))])}},{key:"action",fn:function(e,a){return n("span",{},[n("a",{attrs:{href:"javascript:;"},on:{click:function(e){return t.handleEditLink(a.id)}}},[t._v("编辑")]),n("a-divider",{attrs:{type:"vertical"}}),n("a-popconfirm",{attrs:{title:"你确定要删除【"+a.name+"】链接？",okText:"确定",cancelText:"取消"},on:{confirm:function(e){return t.handleDeleteLink(a.id)}}},[n("a",{attrs:{href:"javascript:;"}},[t._v("删除")])])],1)}}])})],1)],1)],1)],1)},i=[],l=(n("b54a"),n("9efd")),o="/api/admin/links",r={listAll:function(){return Object(l["a"])({url:"".concat(o),method:"get"})},create:function(t){return Object(l["a"])({url:o,data:t,method:"post"})},get:function(t){return Object(l["a"])({url:"".concat(o,"/").concat(t),method:"get"})},update:function(t,e){return Object(l["a"])({url:"".concat(o,"/").concat(t),data:e,method:"put"})},delete:function(t){return Object(l["a"])({url:"".concat(o,"/").concat(t),method:"delete"})}},c=r,s=[{title:"名称",dataIndex:"name",scopedSlots:{customRender:"name"}},{title:"网址",dataIndex:"url",scopedSlots:{customRender:"url"}},{title:"分组",dataIndex:"team"},{title:"操作",key:"action",scopedSlots:{customRender:"action"}}],d={data:function(){return{formType:"create",data:[],loading:!1,columns:s,links:[],link:{}}},computed:{title:function(){return this.link.id?"修改友情链接":"添加友情链接"}},created:function(){this.loadLinks()},methods:{loadLinks:function(){var t=this;this.loading=!0,c.listAll().then(function(e){t.links=e.data.data,t.loading=!1})},handleSaveClick:function(){this.createOrUpdateLink()},handleAddLink:function(){this.formType="create",this.link={}},handleEditLink:function(t){var e=this;c.get(t).then(function(t){e.link=t.data.data,e.formType="update"})},handleDeleteLink:function(t){var e=this;c.delete(t).then(function(t){e.$message.success("删除成功！"),e.loadLinks()})},createOrUpdateLink:function(){var t=this;this.link.id?c.update(this.link.id,this.link).then(function(e){t.$message.success("更新成功！"),t.loadLinks()}):c.create(this.link).then(function(e){t.$message.success("保存成功！"),t.loadLinks()}),this.handleAddLink()}}},u=d,p=n("2877"),m=Object(p["a"])(u,a,i,!1,null,"5cfa7a9e",null);e["default"]=m.exports}}]);