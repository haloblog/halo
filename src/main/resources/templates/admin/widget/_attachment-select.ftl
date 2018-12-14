<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <link rel="stylesheet" href="/static/plugins/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="/static/plugins/toast/css/jquery.toast.min.css">
    <link rel="stylesheet" href="/static/plugins/fileinput/fileinput.min.css">
    <link rel="stylesheet" href="/static/css/AdminLTE.min.css">
</head>
<body>
<div class="container-fluid">
    <section class="content">
        <div class="nav-tabs-custom">
            <ul class="nav nav-tabs">
                <li class="active">
                    <a href="#allAttach" data-toggle="tab"><@spring.message code='admin.attachments.modal.select.tab.all-attach' /></a>
                </li>
                <li>
                    <a href="#uploadAttach" data-toggle="tab"><@spring.message code='admin.attachments.modal.select.tab.upload' /></a>
                </li>
            </ul>
            <div class="tab-content">
                <div class="tab-pane active" id="allAttach">
                    <div class="row">
                        <#list attachments.content as attachment>
                            <div class="col-lg-2 col-md-2 col-sm-6 col-xs-6 div-thumbnail">
                                <a href="#" class="thumbnail" onclick="doTransport('${attachment.attachPath}')">
                                    <img src="${attachment.attachSmallPath}" class="img-responsive">
                                </a>
                            </div>
                        </#list>
                        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                            <div class="no-margin pull-left">
                                <@spring.message code='admin.pageinfo.text.no' />${attachments.number+1}/${attachments.totalPages}<@spring.message code='admin.pageinfo.text.page' />
                            </div>
                            <div class="btn-group pull-right btn-group-sm" role="group">
                                <a class="btn btn-default <#if !attachments.hasPrevious()>disabled</#if>" href="/admin/attachments/select?id=${id}" >
                                    <@spring.message code='admin.pageinfo.btn.first' />
                                </a>
                                <a class="btn btn-default <#if !attachments.hasPrevious()>disabled</#if>" href="/admin/attachments/select?page=${attachments.number-1}&id=${id}" >
                                    <@spring.message code='admin.pageinfo.btn.pre' />
                                </a>
                                <a class="btn btn-default <#if !attachments.hasNext()>disabled</#if>" href="/admin/attachments/select?page=${attachments.number+1}&id=${id}">
                                    <@spring.message code='admin.pageinfo.btn.next' />
                                </a>
                                <a class="btn btn-default <#if !attachments.hasNext()>disabled</#if>" href="/admin/attachments/select?page=${attachments.totalPages-1}&id=${id}">
                                    <@spring.message code='admin.pageinfo.btn.last' />
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="tab-pane" id="uploadAttach">
                    <div class="row" id="uploadForm">
                        <div class="col-md-12">
                            <div class="form-group">
                                <div class="file-loading">
                                    <input id="uploadImg" class="file-loading" type="file" multiple name="file">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</div>
</body>
<script src="/static/plugins/jquery/jquery.min.js"></script>
<script src="/static/plugins/bootstrap/js/bootstrap.min.js"></script>
<script src="/static/plugins/fileinput/fileinput.min.js"></script>
<#if (options.blog_locale!'zh_CN')=='zh_CN'>
<script src="/static/plugins/fileinput/zh.min.js"></script>
</#if>
<script src="/static/plugins/toast/js/jquery.toast.min.js"></script>
<script src="/static/js/halo.min.js"></script>
<script>
    var halo = new $.halo();
    $('#uploadImg').fileinput({
        language: 'zh',
        uploadUrl: '/admin/attachments/upload',
        uploadAsync: true,
        allowedFileExtensions: ['jpg','gif','png','jpeg','svg'],
        maxFileCount: 10,
        enctype : 'multipart/form-data',
        showClose: false
    }).on("filebatchuploadcomplete",function (event, files, extra) {
        $("#uploadForm").hide(400);
        halo.showMsgAndReload('上传成功！','success',1000);
    });
    function doTransport(url) {
        parent.$('#${id}').val(url);
        parent.$('#${id}').attr("src",url);
        parent.layer.closeAll();
    }
</script>
</html>
