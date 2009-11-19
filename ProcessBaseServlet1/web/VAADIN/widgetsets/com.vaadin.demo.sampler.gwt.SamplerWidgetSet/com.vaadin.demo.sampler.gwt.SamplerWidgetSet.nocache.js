function com_vaadin_demo_sampler_gwt_SamplerWidgetSet(){
    var l='',F='" for "gwt:onLoadErrorFn"',D='" for "gwt:onPropertyErrorFn"',n='"><\/script>',p='#',r='/',xb='1604AE5C8E352FEAA6B5780A8C351151.cache.html',vb='39545322A86B59A01886E45FAA38972B.cache.html',zb='5688589AA2638480BC709DBE2A411476.cache.html',wb='6A55CB686FDB0B18064BFD102D983AE0.cache.html',hc='<script defer="defer">com_vaadin_demo_sampler_gwt_SamplerWidgetSet.onInjectionDone(\'com.vaadin.demo.sampler.gwt.SamplerWidgetSet\')<\/script>',kc='<script id="',fc='<script language="javascript" src="',cc='<script language="javascript" src="http://www.google-analytics.com/ga.js"><\/script>',A='=',q='?',ub='A2E5ABB819C42E72DE5D6CA6CA7D5A09.cache.html',C='Bad handler "',tb='C0F07A803800C1652697538AF798266D.cache.html',ac='DOMContentLoaded',o='SCRIPT',jc='__gwt_marker_com.vaadin.demo.sampler.gwt.SamplerWidgetSet',s='base',nb='begin',cb='bootstrap',u='clear.cache.gif',m='com.vaadin.demo.sampler.gwt.SamplerWidgetSet',z='content',ic='end',mb='gecko',ob='gecko1_8',yb='gwt.hybrid',E='gwt:onLoadErrorFn',B='gwt:onPropertyErrorFn',y='gwt:property',Fb='head',rb='hosted.html?com_vaadin_demo_sampler_gwt_SamplerWidgetSet',Eb='href',bc='http://www.google-analytics.com/ga.js',lb='ie6',kb='ie8',ab='iframe',t='img',bb="javascript:''",Bb='link',qb='loadExternalRefs',v='meta',eb='moduleRequested',dc='moduleStartup',jb='msie',w='name',gb='opera',db='position:absolute;width:0;height:0;border:none',Ab='prettify/prettify.css',ec='prettify/prettify.js',gc='prettify/prettify.js"><\/script>',Cb='rel',ib='safari',sb='selectingPermutation',x='startup',Db='stylesheet',pb='unknown',fb='user.agent',hb='webkit';var mc=window,k=document,lc=mc.__gwtStatsEvent?function(a){
        return mc.__gwtStatsEvent(a)
        }:null,ad,wc,rc,qc=l,zc={},dd=[],Fc=[],pc=[],Cc,Ec;lc&&lc({
        moduleName:m,
        subSystem:x,
        evtGroup:cb,
        millis:(new Date()).getTime(),
        type:nb
    });if(!mc.__gwt_stylesLoaded){
        mc.__gwt_stylesLoaded={}
        }if(!mc.__gwt_scriptsLoaded){
        mc.__gwt_scriptsLoaded={}
        }function vc(){
        var b=false;try{
            b=mc.external&&(mc.external.gwtOnLoad&&mc.location.search.indexOf(yb)==-1)
            }catch(a){}vc=function(){
            return b
            };return b
        }
    function yc(){
        if(ad&&wc){
            var c=k.getElementById(m);var b=c.contentWindow;if(vc()){
                b.__gwt_getProperty=function(a){
                    return sc(a)
                    }
                }com_vaadin_demo_sampler_gwt_SamplerWidgetSet=null;b.gwtOnLoad(Cc,m,qc);lc&&lc({
                moduleName:m,
                subSystem:x,
                evtGroup:dc,
                millis:(new Date()).getTime(),
                type:ic
            })
            }
        }
    function tc(){
        var j,h=jc,i;k.write(kc+h+n);i=k.getElementById(h);j=i&&i.previousSibling;while(j&&j.tagName!=o){
            j=j.previousSibling
            }function f(b){
            var a=b.lastIndexOf(p);if(a==-1){
                a=b.length
                }var c=b.indexOf(q);if(c==-1){
                c=b.length
                }var d=b.lastIndexOf(r,Math.min(c,a));return d>=0?b.substring(0,d+1):l
            }
        ;if(j&&j.src){
            qc=f(j.src)
            }if(qc==l){
            var e=k.getElementsByTagName(s);if(e.length>0){
                qc=e[e.length-1].href
                }else{
                qc=f(k.location.href)
                }
            }else if(qc.match(/^\w+:\/\//)){}else{
            var g=k.createElement(t);g.src=qc+u;qc=f(g.src)
            }if(i){
            i.parentNode.removeChild(i)
            }
        }
    function Dc(){
        var f=document.getElementsByTagName(v);for(var d=0,g=f.length;d<g;++d){
            var e=f[d],h=e.getAttribute(w),b;if(h){
                if(h==y){
                    b=e.getAttribute(z);if(b){
                        var i,c=b.indexOf(A);if(c>=0){
                            h=b.substring(0,c);i=b.substring(c+1)
                            }else{
                            h=b;i=l
                            }zc[h]=i
                        }
                    }else if(h==B){
                    b=e.getAttribute(z);if(b){
                        try{
                            Ec=eval(b)
                            }catch(a){
                            alert(C+b+D)
                            }
                        }
                    }else if(h==E){
                    b=e.getAttribute(z);if(b){
                        try{
                            Cc=eval(b)
                            }catch(a){
                            alert(C+b+F)
                            }
                        }
                    }
                }
            }
        }
    function cd(d,e){
        var a=pc;for(var b=0,c=d.length-1;b<c;++b){
            a=a[d[b]]||(a[d[b]]=[])
            }a[d[c]]=e
        }
    function sc(d){
        var e=Fc[d](),b=dd[d];if(e in b){
            return e
            }var a=[];for(var c in b){
            a[b[c]]=c
            }if(Ec){
            Ec(d,a,e)
            }throw null
        }
    var uc;function xc(){
        if(!uc){
            uc=true;var a=k.createElement(ab);a.src=bb;a.id=m;a.style.cssText=db;a.tabIndex=-1;k.body.appendChild(a);lc&&lc({
                moduleName:m,
                subSystem:x,
                evtGroup:dc,
                millis:(new Date()).getTime(),
                type:eb
            });a.contentWindow.location.replace(qc+bd)
            }
        }
    Fc[fb]=function(){
        var d=navigator.userAgent.toLowerCase();var b=function(a){
            return parseInt(a[1])*1000+parseInt(a[2])
            };if(d.indexOf(gb)!=-1){
            return gb
            }else if(d.indexOf(hb)!=-1){
            return ib
            }else if(d.indexOf(jb)!=-1){
            if(document.documentMode>=8){
                return kb
                }else{
                var c=/msie ([0-9]+)\.([0-9]+)/.exec(d);if(c&&c.length==3){
                    var e=b(c);if(e>=6000){
                        return lb
                        }
                    }
                }
            }else if(d.indexOf(mb)!=-1){
            var c=/rv:([0-9]+)\.([0-9]+)/.exec(d);if(c&&c.length==3){
                if(b(c)>=1008)return ob
                    }return mb
            }return pb
        };dd[fb]={
        gecko:0,
        gecko1_8:1,
        ie6:2,
        ie8:3,
        opera:4,
        safari:5
    };com_vaadin_demo_sampler_gwt_SamplerWidgetSet.onScriptLoad=function(){
        if(uc){
            wc=true;yc()
            }
        };com_vaadin_demo_sampler_gwt_SamplerWidgetSet.onInjectionDone=function(){
        ad=true;lc&&lc({
            moduleName:m,
            subSystem:x,
            evtGroup:qb,
            millis:(new Date()).getTime(),
            type:ic
        });yc()
        };tc();var bd;if(vc()){
        if(mc.external.initModule&&mc.external.initModule(m)){
            mc.location.reload();return
        }bd=rb
        }Dc();lc&&lc({
        moduleName:m,
        subSystem:x,
        evtGroup:cb,
        millis:(new Date()).getTime(),
        type:sb
    });if(!bd){
        try{
            cd([mb],tb);cd([lb],ub);cd([ib],vb);cd([kb],wb);cd([gb],xb);cd([ob],zb);bd=pc[sc(fb)]
            }catch(a){
            return
        }
        }var Bc;function Ac(){
        if(!rc){
            rc=true;if(!__gwt_stylesLoaded[Ab]){
                var a=k.createElement(Bb);__gwt_stylesLoaded[Ab]=a;a.setAttribute(Cb,Db);a.setAttribute(Eb,qc+Ab);k.getElementsByTagName(Fb)[0].appendChild(a)
                }yc();if(k.removeEventListener){
                k.removeEventListener(ac,Ac,false)
                }if(Bc){
                clearInterval(Bc)
                }
            }
        }
    if(k.addEventListener){
        k.addEventListener(ac,function(){
            xc();Ac()
            },false)
        }var Bc=setInterval(function(){
        if(/loaded|complete/.test(k.readyState)){
            xc();Ac()
            }
        },50);lc&&lc({
        moduleName:m,
        subSystem:x,
        evtGroup:cb,
        millis:(new Date()).getTime(),
        type:ic
    });lc&&lc({
        moduleName:m,
        subSystem:x,
        evtGroup:qb,
        millis:(new Date()).getTime(),
        type:nb
    });if(!__gwt_scriptsLoaded[bc]){
        __gwt_scriptsLoaded[bc]=true;document.write(cc)
        }if(!__gwt_scriptsLoaded[ec]){
        __gwt_scriptsLoaded[ec]=true;document.write(fc+qc+gc)
        }k.write(hc)
    }
com_vaadin_demo_sampler_gwt_SamplerWidgetSet();