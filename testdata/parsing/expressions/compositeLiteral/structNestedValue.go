package main
var e = struct{a, b int}{{1, 2}}
-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  VarDeclarationsImpl
    PsiElement(KEYWORD_VAR)('var')
    PsiWhiteSpace(' ')
    VarDeclarationImpl
      LiteralIdentifierImpl
        PsiElement(IDENTIFIER)('e')
      PsiWhiteSpace(' ')
      PsiElement(=)('=')
      PsiWhiteSpace(' ')
      LiteralExpressionImpl
        LiteralCompositeImpl
          TypeStructImpl
            PsiElement(KEYWORD_STRUCT)('struct')
            PsiElement({)('{')
            TypeStructFieldImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('a')
              PsiElement(,)(',')
              PsiWhiteSpace(' ')
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('b')
              PsiWhiteSpace(' ')
              TypeNameImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('int')
            PsiElement(})('}')
          LiteralCompositeValueImpl
            PsiElement({)('{')
            LiteralCompositeElementImpl
              LiteralCompositeValueImpl
                PsiElement({)('{')
                LiteralCompositeElementImpl
                  LiteralExpressionImpl
                    LiteralIntegerImpl
                      PsiElement(LITERAL_INT)('1')
                PsiElement(,)(',')
                PsiWhiteSpace(' ')
                LiteralCompositeElementImpl
                  LiteralExpressionImpl
                    LiteralIntegerImpl
                      PsiElement(LITERAL_INT)('2')
                PsiElement(})('}')
            PsiElement(})('}')