(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-2d0b64bf"],{"1d0f":function(t,e,a){"use strict";a.r(e);var n=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("page-view",[a("a-card",{attrs:{bordered:!1}},[a("div",{staticClass:"table-page-search-wrapper"},[a("a-form",{attrs:{layout:"inline"}},[a("a-row",{attrs:{gutter:48}},[a("a-col",{attrs:{md:6,sm:24}},[a("a-form-item",{attrs:{label:"关键词"}},[a("a-input",{model:{value:t.queryParam.keyword,callback:function(e){t.$set(t.queryParam,"keyword",e)},expression:"queryParam.keyword"}})],1)],1),a("a-col",{attrs:{md:6,sm:24}},[a("a-form-item",{attrs:{label:"评论状态"}},[a("a-select",{attrs:{placeholder:"请选择评论状态"},on:{change:function(e){return t.loadComments(t.isSearch)}},model:{value:t.queryParam.status,callback:function(e){t.$set(t.queryParam,"status",e)},expression:"queryParam.status"}},t._l(Object.keys(t.commentStatus),function(e){return a("a-select-option",{key:e,attrs:{value:e}},[t._v(t._s(t.commentStatus[e].text))])}),1)],1)],1),a("a-col",{attrs:{md:12,sm:24}},[a("span",{staticClass:"table-page-search-submitButtons"},[a("a-button",{attrs:{type:"primary"},on:{click:function(e){return t.loadComments(t.isSearch)}}},[t._v("查询")]),a("a-button",{staticStyle:{"margin-left":"8px"},on:{click:t.handleResetParam}},[t._v("重置")])],1)])],1)],1)],1),a("div",{staticClass:"table-operator"},[a("a-dropdown",{directives:[{name:"show",rawName:"v-show",value:null!=t.queryParam.status&&""!=t.queryParam.status,expression:"queryParam.status!=null && queryParam.status!=''"}]},[a("a-menu",{attrs:{slot:"overlay"},slot:"overlay"},["AUDITING"===t.queryParam.status?a("a-menu-item",{key:"1"},[a("a",{attrs:{href:"javascript:void(0);"},on:{click:t.handlePublishMore}},[t._v("\n              通过\n            ")])]):t._e(),"PUBLISHED"===t.queryParam.status||"AUDITING"===t.queryParam.status?a("a-menu-item",{key:"2"},[a("a",{attrs:{href:"javascript:void(0);"},on:{click:t.handleRecycleMore}},[t._v("\n              移到回收站\n            ")])]):t._e(),"RECYCLE"===t.queryParam.status?a("a-menu-item",{key:"3"},[a("a",{attrs:{href:"javascript:void(0);"},on:{click:t.handleDeleteMore}},[t._v("\n              永久删除\n            ")])]):t._e()],1),a("a-button",[t._v("\n          批量操作\n          "),a("a-icon",{attrs:{type:"down"}})],1)],1)],1),a("div",{staticStyle:{"margin-top":"15px"}},[a("a-table",{attrs:{rowKey:function(t){return t.id},rowSelection:{onChange:t.onSelectionChange,getCheckboxProps:t.getCheckboxProps},columns:t.columns,dataSource:t.formattedComments,loading:t.commentsLoading,pagination:!1},scopedSlots:t._u([{key:"content",fn:function(e){return a("p",{domProps:{innerHTML:t._s(e)}})}},{key:"status",fn:function(e){return a("span",{},[a("a-badge",{attrs:{status:e.status}}),t._v("\n          "+t._s(e.text)+"\n        ")],1)}},{key:"post",fn:function(e){return a("a",{attrs:{href:t.options.blog_url+"/archives/"+e.url,target:"_blank"}},[t._v(t._s(e.title))])}},{key:"createTime",fn:function(e){return a("span",{},[t._v(t._s(t._f("timeAgo")(e)))])}},{key:"action",fn:function(e,n){return a("span",{},["AUDITING"===n.status?a("a-dropdown",{attrs:{trigger:["click"]}},[a("a",{staticClass:"ant-dropdown-link",attrs:{href:"javascript:void(0);"}},[t._v("通过")]),a("a-menu",{attrs:{slot:"overlay"},slot:"overlay"},[a("a-menu-item",{key:"1"},[a("a",{attrs:{href:"javascript:void(0);"},on:{click:function(e){return t.handleEditStatusClick(n.id,"PUBLISHED")}}},[t._v("通过")])]),a("a-menu-item",{key:"2"},[a("a",{attrs:{href:"javascript:void(0);"},on:{click:function(e){return t.handleReplyAndPassClick(n)}}},[t._v("通过并回复")])])],1)],1):"PUBLISHED"===n.status?a("a",{attrs:{href:"javascript:void(0);"},on:{click:function(e){return t.handleReplyClick(n)}}},[t._v("回复")]):"RECYCLE"===n.status?a("a-popconfirm",{attrs:{title:"你确定要还原该评论？",okText:"确定",cancelText:"取消"},on:{confirm:function(e){return t.handleEditStatusClick(n.id,"PUBLISHED")}}},[a("a",{attrs:{href:"javascript:;"}},[t._v("还原")])]):t._e(),a("a-divider",{attrs:{type:"vertical"}}),"PUBLISHED"===n.status||"AUDITING"===n.status?a("a-popconfirm",{attrs:{title:"你确定要将该评论移到回收站？",okText:"确定",cancelText:"取消"},on:{confirm:function(e){return t.handleEditStatusClick(n.id,"RECYCLE")}}},[a("a",{attrs:{href:"javascript:;"}},[t._v("回收站")])]):"RECYCLE"===n.status?a("a-popconfirm",{attrs:{title:"你确定要永久删除该评论？",okText:"确定",cancelText:"取消"},on:{confirm:function(e){return t.handleDeleteClick(n.id)}}},[a("a",{attrs:{href:"javascript:;"}},[t._v("删除")])]):t._e()],1)}}])}),a("div",{staticClass:"page-wrapper"},[a("a-pagination",{staticClass:"pagination",attrs:{total:t.pagination.total,pageSizeOptions:["1","2","5","10","20","50","100"],showSizeChanger:""},on:{showSizeChange:t.handlePaginationChange,change:t.handlePaginationChange}})],1)],1)]),t.selectComment?a("a-modal",{attrs:{title:"回复给："+t.selectComment.author},on:{close:t.onReplyClose},model:{value:t.replyCommentVisible,callback:function(e){t.replyCommentVisible=e},expression:"replyCommentVisible"}},[a("template",{slot:"footer"},[a("a-button",{key:"submit",attrs:{type:"primary"},on:{click:t.handleCreateClick}},[t._v("\n        回复\n      ")])],1),a("a-form",{attrs:{layout:"vertical"}},[a("a-form-item",[a("a-input",{attrs:{type:"textarea",autosize:{minRows:8}},model:{value:t.replyComment.content,callback:function(e){t.$set(t.replyComment,"content",e)},expression:"replyComment.content"}})],1)],1)],2):t._e()],1)},s=[],o=(a("612f"),a("ab56"),a("680a")),i=a("063c"),r=a("482b"),l=a("ae4d"),c=a.n(l),u=[{title:"昵称",dataIndex:"author"},{title:"内容",dataIndex:"content",scopedSlots:{customRender:"content"}},{title:"状态",className:"status",dataIndex:"statusProperty",scopedSlots:{customRender:"status"}},{title:"评论页面",dataIndex:"post",scopedSlots:{customRender:"post"}},{title:"日期",dataIndex:"createTime",scopedSlots:{customRender:"createTime"}},{title:"操作",dataIndex:"action",width:"150px",scopedSlots:{customRender:"action"}}],m={name:"CommentList",components:{PageView:o["c"]},data:function(){return{columns:u,replyCommentVisible:!1,pagination:{current:1,pageSize:10,sort:null},queryParam:{page:0,size:10,sort:null,keyword:null,status:null},selectedRowKeys:[],selectedRows:[],comments:[],selectComment:{},replyComment:{},commentsLoading:!1,commentStatus:i["a"].commentStatus,options:[],keys:["blog_url"]}},computed:{formattedComments:function(){var t=this;return this.comments.map(function(e){return e.statusProperty=t.commentStatus[e.status],e.content=c()(e.content,{sanitize:!0}),e})}},created:function(){this.loadComments(),this.loadOptions()},methods:{loadComments:function(t){var e=this;this.commentsLoading=!0,this.queryParam.page=this.pagination.current-1,this.queryParam.size=this.pagination.pageSize,this.queryParam.sort=this.pagination.sort,t&&(this.queryParam.page=0),i["a"].query(this.queryParam).then(function(t){e.comments=t.data.data.content,e.pagination.total=t.data.data.total,e.commentsLoading=!1})},loadOptions:function(){var t=this;r["a"].listAll(this.keys).then(function(e){t.options=e.data.data})},handleEditComment:function(t){this.$message.success("编辑")},handleEditStatusClick:function(t,e){var a=this;i["a"].updateStatus(t,e).then(function(t){a.$message.success("操作成功！"),a.loadComments()})},handleDeleteClick:function(t){var e=this;i["a"].delete(t).then(function(t){e.$message.success("删除成功！"),e.loadComments()})},handleReplyAndPassClick:function(t){this.handleReplyClick(t),this.handleEditStatusClick(t.id,"PUBLISHED")},handleReplyClick:function(t){this.selectComment=t,this.replyCommentVisible=!0,this.replyComment.parentId=t.id,this.replyComment.postId=t.post.id},handleCreateClick:function(){var t=this;i["a"].create(this.replyComment).then(function(e){t.$message.success("回复成功！"),t.replyComment={},t.selectComment={},t.replyCommentVisible=!1,t.loadComments()})},handlePaginationChange:function(t,e){this.$log.debug("Current: ".concat(t,", PageSize: ").concat(e)),this.pagination.current=t,this.pagination.pageSize=e,this.loadComments()},handleResetParam:function(){this.queryParam.keyword=null,this.queryParam.status=null,this.loadComments()},handlePublishMore:function(){var t=this;this.selectedRowKeys.length<=0&&this.$message.success("请至少选择一项！");for(var e=function(e){var a=t.selectedRowKeys[e];i["a"].updateStatus(a,"PUBLISHED").then(function(e){t.$log.debug("commentId: ".concat(a,", status: PUBLISHED"))})},a=0;a<this.selectedRowKeys.length;a++)e(a);this.loadComments()},handleRecycleMore:function(){var t=this;this.selectedRowKeys.length<=0&&this.$message.success("请至少选择一项！");for(var e=function(e){var a=t.selectedRowKeys[e];i["a"].updateStatus(a,"RECYCLE").then(function(e){t.$log.debug("commentId: ".concat(a,", status: RECYCLE"))})},a=0;a<this.selectedRowKeys.length;a++)e(a);this.loadComments()},handleDeleteMore:function(){var t=this;this.selectedRowKeys.length<=0&&this.$message.success("请至少选择一项！");for(var e=function(e){var a=t.selectedRowKeys[e];i["a"].delete(a).then(function(e){t.$log.debug("delete: ".concat(a))})},a=0;a<this.selectedRowKeys.length;a++)e(a);this.loadComments()},onReplyClose:function(){this.replyComment={},this.selectComment={},this.replyCommentVisible=!1},onSelectionChange:function(t){this.selectedRowKeys=t,this.$log.debug("SelectedRowKeys: ".concat(t))},getCheckboxProps:function(t){return{props:{disabled:"RECYCLE"===t.status,name:t.author}}}}},d=m,h=a("17cc"),p=Object(h["a"])(d,n,s,!1,null,null,null);e["default"]=p.exports}}]);