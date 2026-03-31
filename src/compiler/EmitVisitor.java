package compiler;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import grammar.firstBaseVisitor;
import grammar.firstParser;

public class EmitVisitor extends firstBaseVisitor<ST> {
    private final STGroup stGroup;

    public EmitVisitor(STGroup group) {
        super();
        this.stGroup = group;
    }

    @Override
    protected ST defaultResult() {
        return stGroup.getInstanceOf("deflt");
    }

    @Override
    protected ST aggregateResult(ST aggregate, ST nextResult) {
        if (nextResult != null) {
            aggregate.add("elem", nextResult);
        }
        return aggregate;
    }

    @Override
    public ST visitFunc_def(firstParser.Func_defContext ctx) {
        var st = stGroup.getInstanceOf("funcdef");
        st.add("name", ctx.name.getText())
            .add("body", visit(ctx.body));
        return st;
    }

    @Override
    public ST visitFunc_call(firstParser.Func_callContext ctx) {
        var st = stGroup.getInstanceOf("funcall");
        st.add("name", ctx.name.getText());
        for (firstParser.LogexprContext arg :ctx.arg) {
            st.add("pars", visit(arg));
        }
        return st;
    }
 /*
    @Override
    public ST visitTerminal(TerminalNode node) {
        return new ST("Terminal node:<n>").add("n", node.getText());
    }
*/
    @Override
    public ST visitIntExpr(firstParser.IntExprContext ctx) {
        ST st = stGroup.getInstanceOf("int");
        st.add("i", ctx.INT().getText());
        return st;
    }

    @Override
    public ST visitParensExpr(firstParser.ParensExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public ST visitAddExpr(firstParser.AddExprContext ctx) {
        if (ctx.ADD() != null) {
            ST st = stGroup.getInstanceOf("dodaj");
            return st.add("p1", visit(ctx.expr(0)))
                    .add("p2", visit(ctx.expr(1)));
        } else {
            ST st = stGroup.getInstanceOf("odejmij");
            return st.add("p1", visit(ctx.expr(0)))
                    .add("p2", visit(ctx.expr(1)));
        }
    }

    @Override
    public ST visitMulExpr(firstParser.MulExprContext ctx) {
        if (ctx.MUL() != null) {
            ST st = stGroup.getInstanceOf("pomnoz");
            return st.add("p1", visit(ctx.expr(0)))
                    .add("p2", visit(ctx.expr(1)));
        } else {
            ST st = stGroup.getInstanceOf("podziel");
            return st.add("p1", visit(ctx.expr(0)))
                    .add("p2", visit(ctx.expr(1)));
        }
    }

    @Override
    public ST visitIdExpr(firstParser.IdExprContext ctx) {
        ST st = stGroup.getInstanceOf("load");
        st.add("n", ctx.ID().getText());
        return st;
    }

    @Override
    public ST visitAssignExpr(firstParser.AssignExprContext ctx) {
        ST st = stGroup.getInstanceOf("assign");
        st.add("n", ctx.ID().getText());
        st.add("p", visit(ctx.expr()));
        return st;
    }

    @Override
    public ST visitVar_decl(firstParser.Var_declContext ctx) {
        if (ctx.expr() == null) {
            ST st = stGroup.getInstanceOf("dek");
            st.add("n", ctx.ID().getText());
            return st;
        } else {
            ST st = stGroup.getInstanceOf("dekinit");
            st.add("n", ctx.ID().getText());
            st.add("p", visit(ctx.expr()));
            return st;
        }
    }
}