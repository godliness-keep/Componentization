package com.godliness.route.compiler;

import com.godliness.route.compiler.utils.CheckEmpty;
import com.godliness.route.compiler.utils.Consts;
import com.godliness.router.annotation.Route;
import com.godliness.router.annotation.model.RouteMeta;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by godliness on 2019-08-29.
 *
 * @author godliness
 * 1、注解处理器要进行注册，编译阶段才会被执行到，注册方式：手动注册、自动注册
 * 这里采用Google提供的工具，自动注册。
 */

/**
 * 注册处理器，这里采用Google的框架完成注册功能
 */
@AutoService(Process.class)
/**
 * 指定处理器接收的参数 替代 {@link AbstractProcessor#getSupportedOptions()}
 * */
@SupportedOptions(Consts.ARGUMENTS_NAME)
/**
 * 指定使用的Java版本 替代 {@link AbstractProcessor#getSupportedSourceVersion()}
 * */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
/**
 * 指定处理器要处理的注解类型 替代 {@link AbstractProcessor#getSupportedAnnotationTypes()}
 * */
@SupportedAnnotationTypes({Consts.ANN_TYPE_ROUTE})
public final class RoutePorcessor extends AbstractProcessor {

    /**
     * key:组名 value:类型
     */
    private Map<String, String> rootMap = new TreeMap();
    private Map<String, List<RouteMeta>> groupMap = new HashMap<>();

    private Messager messager;
    /**
     * 节点工具类（类、函数、属性都是节点）
     */
    private Elements elementUtils;
    /**
     * 类型工具类
     */
    private Types typeUtils;
    /**
     * 文件生成器 类/资源
     */
    private Filer filerUtils;

    private String moduelName;

    /**
     * 相当于构造函数
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        filerUtils = processingEnvironment.getFiler();
        Map<String, String> options = processingEnvironment.getOptions();
        if (!CheckEmpty.isEmpty(options)) {
            moduelName = options.get(Consts.ARGUMENTS_NAME);
        }
        if (CheckEmpty.isEmpty(moduelName)) {
            throw new RuntimeException("The module name was not retrieved");
        }
    }

    /**
     * 能够该注解处理的元素都会回调到这里
     * set是注解的节点
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!CheckEmpty.isEmpty(set)) {
            //获取所有被 Route 注解的元素集合，被注解的节点
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Route.class);
            if (!CheckEmpty.isEmpty(elements)) {
                //处理 Route 注解
                try {
                    parseRoute(elements);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //当前消费掉
            return true;
        }
        return false;
    }

    private void parseRoute(Set<? extends Element> elements) throws Exception {
        //通过节点工具，获取一个Activity的类型
        final TypeElement activityElement = elementUtils.getTypeElement(Consts.ACTIVITY);
        final TypeMirror activityMirror = activityElement.asType();

        final TypeElement serviceElement = elementUtils.getTypeElement(Consts.ISERVICE);
        final TypeMirror serviceMirror = serviceElement.asType();

        for (Element element : elements) {
            RouteMeta meta;
            //类信息
            final TypeMirror mirror = element.asType();
            final Route route = element.getAnnotation(Route.class);
            if (typeUtils.isSubtype(mirror, activityMirror)) {
                //Activity类型
                meta = new RouteMeta(RouteMeta.Type.ACTIVITY, element, route);
            } else if (typeUtils.isSubtype(mirror, serviceMirror)) {
                //Service类型
                meta = new RouteMeta(RouteMeta.Type.ISERVICE, element, route);
            } else {
                throw new RuntimeException("[Just Support Activity/IService Route] :" + element);
            }
            //记录分组
            categories(meta);
        }

        final TypeElement routeGroupElement = elementUtils.getTypeElement(Consts.IROUTE_GROUP);
        final TypeElement routeRootElement = elementUtils.getTypeElement(Consts.IROUTE_ROOT);
        generatedGroup(routeGroupElement);
        generatedRoot(routeRootElement, routeGroupElement);

    }

    private void generatedGroup(TypeElement element) throws IOException {
        //参数类型
        final ParameterizedTypeName ptn = ParameterizedTypeName
                .get(ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ClassName.get(RouteMeta.class));

        //方法完整参数
        final ParameterSpec groupSpec = ParameterSpec.builder(ptn, "atlas").build();

        for (Map.Entry<String, List<RouteMeta>> entry : groupMap.entrySet()) {
            final MethodSpec.Builder loadIntoMethodBuilder = MethodSpec.methodBuilder(Consts.METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(groupSpec);

            final String groupName = entry.getKey();
            final List<RouteMeta> groupMetas = entry.getValue();
            for (RouteMeta meta : groupMetas) {
                loadIntoMethodBuilder.addStatement(
                        "atlas.put($S, $T.build($T.$L,$T.class, $S, $S))",
                        meta.getPath(),
                        ClassName.get(RouteMeta.class),
                        ClassName.get(RouteMeta.Type.class),
                        meta.getType(),
                        ClassName.get((TypeElement) meta.getElement()),
                        meta.getPath().toLowerCase(),
                        meta.getGroup().toLowerCase()
                );
            }

            final String groupClassName = Consts.NAME_OF_GROUP + groupName;
            JavaFile.builder(
                    Consts.PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(groupClassName).
                            addSuperinterface(ClassName.get(element)).
                            addModifiers(Modifier.PUBLIC).
                            addMethod(loadIntoMethodBuilder.build())
                            .build()
            ).build().writeTo(filerUtils);

            //分组名和生成的对应的Group类类名
            rootMap.put(groupClassName, groupClassName);
        }
    }

    private void generatedRoot(TypeElement root, TypeElement group) throws IOException {
        final ParameterizedTypeName routes = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(group))
                )
        );

        final ParameterSpec rootParamSpec = ParameterSpec.builder(routes, "routes").build();

        final MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(
                Consts.METHOD_LOAD_INTO).
                addAnnotation(Override.class).
                addModifiers(Modifier.PUBLIC).
                addParameter(rootParamSpec);

        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            loadIntoMethodOfRootBuilder.addStatement(
                    "routes.put($S, $T.class)",
                    entry.getKey(),
                    ClassName.get(Consts.PACKAGE_OF_GENERATE_FILE,
                            entry.getValue())
            );
        }

        final String rootClassName = Consts.NAME_OF_ROOT + moduelName;
        JavaFile.builder(Consts.PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(rootClassName)
                        .addSuperinterface(ClassName.get(root))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(loadIntoMethodOfRootBuilder.build())
                        .build()
        ).build().writeTo(filerUtils);
    }

    private void categories(RouteMeta meta) {
        if (routeVerify(meta)) {
            final String group = meta.getGroup();
            final List<RouteMeta> metas = groupMap.get(group);
            if (CheckEmpty.isEmpty(metas)) {
                final List<RouteMeta> newMetas = new ArrayList<>();
                newMetas.add(meta);
                groupMap.put(group, newMetas);
            } else {
                metas.add(meta);
            }
        } else {
        }
    }

    /**
     * 验证Route
     */
    private boolean routeVerify(RouteMeta meta) {
        final String path = meta.getPath();
        final String group = meta.getGroup();
        if (CheckEmpty.isEmpty(path) || !path.startsWith("/")) {
            return false;
        }
        if (CheckEmpty.isEmpty(group)) {
            final String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (CheckEmpty.isEmpty(defaultGroup)) {
                return false;
            }
            meta.setGroup(defaultGroup);
            return true;
        }
        return true;
    }
}
