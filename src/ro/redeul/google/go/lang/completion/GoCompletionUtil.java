package ro.redeul.google.go.lang.completion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.AdapterProcessor;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Function;
import com.intellij.util.PlatformIcons;
import com.intellij.util.Processor;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.lang.completion.insertHandler.KeywordInsertionHandler;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.utils.GoFileUtils;
import ro.redeul.google.go.sdk.GoSdkUtil;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Aug 22, 2010
 */
public class GoCompletionUtil {

    public static LookupElement[] resolveSdkPackagesForPath(Project project, PsiFile containingFile, String path) {
        String currentPath = cleanupImportPath(path);

        VirtualFile virtualFile = containingFile.getVirtualFile();
        if (virtualFile == null) {
            return LookupElement.EMPTY_ARRAY;
        }

        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        Module module = projectFileIndex.getModuleForFile(virtualFile);
        if (module == null) {
            return LookupElement.EMPTY_ARRAY;
        }

        Sdk sdk = GoSdkUtil.getGoogleGoSdkForModule(module);
        if (sdk == null) {
            return LookupElement.EMPTY_ARRAY;
        }

        Set<String> completions = new HashSet<String>();
        VirtualFile roots[] = sdk.getRootProvider().getFiles(OrderRootType.CLASSES);

        for (VirtualFile root : roots) {

            CommonProcessors.CollectUniquesProcessor<VirtualFile> processor = new CommonProcessors.CollectUniquesProcessor<VirtualFile>() {
                @Override
                public boolean process(VirtualFile file) {
                    if ((!file.isDirectory()) && file.getName().matches(".*\\.a$")) {
                        super.process(file);
                    }

                    return true;
                }
            };

            VfsUtil.processFilesRecursively(root, processor);

            for (VirtualFile child : processor.getResults()) {
                completions.add(VfsUtil.getRelativePath(child, root, '/').replaceAll(".a$", ""));
            }
        }

        List<LookupElement> list = new ArrayList<LookupElement>();
        for (String completion : completions) {
            list.add(LookupElementBuilder.create(completion));
        }

        return list.toArray(new LookupElement[list.size()]);
    }

    private static String cleanupImportPath(String currentPath) {
        return currentPath.replaceAll("^\"", "").replaceAll("\"$", "");
    }

    public static Collection<LookupElementBuilder> resolveLocalPackagesForPath(final Project project, PsiFile containingFile, String currentPath) {

        String importPath = cleanupImportPath(currentPath);

        final VirtualFile targetFile = containingFile.getVirtualFile();
        if (targetFile == null) {
            return Collections.emptyList();
        }

        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        Module module = projectFileIndex.getModuleForFile(targetFile);
        if (module == null) {
            return Collections.emptyList();
        }

        final PsiManager psiManager = PsiManager.getInstance(project);

        CommonProcessors.CollectUniquesProcessor<String> localPackages = new CommonProcessors.CollectUniquesProcessor<String>();

        Function<VirtualFile, String> convertor = new Function<VirtualFile, String>() {
            public String fun(VirtualFile virtualFile) {

                GoFile goFile = (GoFile) psiManager.findFile(virtualFile);
                if ( goFile == null ) {
                    return "";
                }

                String packageName = goFile.getPackage().getPackageName();

                // in the same folder as the target file we just import the package
                if ( targetFile.getParent().equals(virtualFile.getParent())) {
                    return packageName;
                }

                String importName = VfsUtil.getRelativePath(virtualFile.getParent(), targetFile.getParent(), '/');
                if ( ! virtualFile.getParent().getName().equals(packageName) ) {
                    importName += "/" + packageName;
                }

                return importName;
            }
        };

        Processor<VirtualFile> processor = new AdapterProcessor<VirtualFile, String>(localPackages, convertor) {
            @Override
            public boolean process(VirtualFile file) {
                if ( file.getFileType() == GoFileType.INSTANCE) {
                    GoFile goFile = (GoFile) psiManager.findFile(file);

                    if ( goFile != null && ! goFile.getPackage().isMainPackage() ) {
                        super.process(file);
                    }

                    return true;
                }

                return true;
            }
        };


        VfsUtil.processFilesRecursively(targetFile.getParent(), processor);

        List<LookupElementBuilder> elements = new ArrayList<LookupElementBuilder>();
        for (String localPackage : localPackages.getResults()) {
            LookupElementBuilder elementBuilder = null;

            if ( importPath.startsWith("./") ) {
                elementBuilder = LookupElementBuilder.create(localPackage).setBold().setTypeText("via project");
            } else if ( importPath.startsWith(".") ) {
                elementBuilder = LookupElementBuilder.create("/" + localPackage).setBold().setTypeText("via project");
            } else {
                elementBuilder = LookupElementBuilder.create("./" + localPackage).setBold().setTypeText("via project");
            }

            elements.add(elementBuilder);
        }

        return elements;
    }

    public static LookupElement keyword(String keyword) {
        return keyword(keyword, new KeywordInsertionHandler());
    }

    public static LookupElement keyword(String keyword, @Nullable InsertHandler<LookupElement> handler) {
        return LookupElementBuilder.create(keyword)
                                   .setBold()
                                   .setTypeText("keyword")
                                   .setInsertHandler(handler);
    }

    public static LookupElement packageElement(String packageName) {
        return LookupElementBuilder.create(packageName)
                                   .setIcon(PlatformIcons.PACKAGE_ICON)
                                   .setTypeText("package");
    }

    public static LookupElement[] getImportedPackagesNames(PsiFile file) {

        if ( ! (file instanceof GoFile) ) {
            return LookupElement.EMPTY_ARRAY;
        }

        GoFile goFile = (GoFile) file;

        List<LookupElement> elements = new ArrayList<LookupElement>();
        for (GoImportDeclaration importDeclaration : GoFileUtils.getImportDeclarations(goFile)) {
            elements.add(packageElement(importDeclaration.getVisiblePackageName()));
        }

        return elements.toArray(new LookupElement[elements.size()]);
    }
}
