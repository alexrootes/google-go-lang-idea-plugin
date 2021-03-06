package ro.redeul.google.go.util;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.PlatformIcons;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.lang.completion.insertHandler.FunctionInsertHandler;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class LookupElementUtil extends GoElementVisitor {

    private LookupElementBuilder lookupElement;

    public LookupElementUtil(LookupElementBuilder lookupElement) {
        this.lookupElement = lookupElement;
    }

    public static LookupElementBuilder createLookupElement(GoPsiElement element) {
        return createLookupElement(element, element.getPresentationText(), element);
    }

    public static LookupElementBuilder createLookupElement(GoPsiElement element, GoPsiElement child) {
        return createLookupElement(element, child.getPresentationText(), child);
    }

    public static LookupElementBuilder createLookupElement(GoPsiElement element, String text, GoPsiElement child) {

        LookupElementBuilder lookup = LookupElementBuilder.create(child, text);

        lookup = lookup.setTailText(element.getPresentationTailText());
        lookup = lookup.setTypeText(element.getPresentationTypeText());

        LookupElementUtil visitor = new LookupElementUtil(lookup);
        element.accept(visitor);

        return visitor.getLookupElement();
    }

    @Override
    public void visitLiteralIdentifier(GoLiteralIdentifier identifier) {
        ((GoPsiElement) identifier.getParent()).accept(this);
    }

    @Override
    public void visitTypeSpec(GoTypeSpec type) {
        type.getType().accept(this);
    }

    @Override
    public void visitInterfaceType(GoPsiTypeInterface type) {
        lookupElement = lookupElement.setIcon(PlatformIcons.INTERFACE_ICON);
    }

    @Override
    public void visitArrayType(GoPsiTypeArray type) {
        lookupElement = lookupElement.setIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitSliceType(GoPsiTypeSlice type) {
        lookupElement = lookupElement.setIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitChannelType(GoPsiTypeChannel type) {
        lookupElement = lookupElement.setIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitStructType(GoPsiTypeStruct type) {
        lookupElement = lookupElement.setIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitMapType(GoPsiTypeMap type) {
        lookupElement = lookupElement.setIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitPointerType(GoPsiTypePointer type) {
        lookupElement = lookupElement.setIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitTypeName(GoPsiTypeName typeName) {
        lookupElement = lookupElement.setIcon(PlatformIcons.CLASS_ICON);
    }

    @Override
    public void visitMethodDeclaration(GoMethodDeclaration declaration) {
        lookupElement = lookupElement
            .setInsertHandler(new FunctionInsertHandler())
            .setIcon(PlatformIcons.METHOD_ICON);
    }

    @Override
    public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
        lookupElement = lookupElement
            .setInsertHandler(new FunctionInsertHandler())
            .setIcon(PlatformIcons.FUNCTION_ICON);
    }

    @Override
    public void visitVarDeclaration(GoVarDeclaration declaration) {
        lookupElement = lookupElement.setIcon(PlatformIcons.VARIABLE_ICON);
    }

    @Override
    public void visitConstDeclaration(GoConstDeclaration declaration) {
        lookupElement = lookupElement.setIcon(GoIcons.CONST_ICON);
    }

    @Override
    public void visitTypeStructField(GoTypeStructField field) {
        lookupElement = lookupElement.setIcon(PlatformIcons.FIELD_ICON);
    }

    @Override
    public void visitTypeStructAnonymousField(GoTypeStructAnonymousField field) {
        lookupElement = lookupElement.setIcon(PlatformIcons.FIELD_ICON);
    }

    public LookupElementBuilder getLookupElement() {
        return lookupElement;
    }
}
