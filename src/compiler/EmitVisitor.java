package compiler;

import grammar.firstBaseVisitor;
import grammar.firstParser;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class EmitVisitor extends firstBaseVisitor<ST> {
    private final STGroup stGroup;

    public EmitVisitor(STGroup group) {
        super();
        this.stGroup = group;
    }

    @Override
    protected ST defaultResult() {
        return new ST("");
    }

    @Override
    protected ST aggregateResult(ST aggregate, ST nextResult) {
        if (nextResult != null) {
            aggregate.add("x", nextResult.render());
        }
        return aggregate;
    }

    @Override
    public ST visitTerminal(TerminalNode node) {
        return new ST("<n>").add("n", node.getText());
    }

    @Override
    public ST visitProg(firstParser.ProgContext ctx) {
        ST st = new ST("<items; separator=\"\\n\">");
        for (firstParser.StatContext s : ctx.stat()) {
            st.add("items", visit(s).render());
        }
        return st;
    }

    @Override
    public ST visitLogexpr_stat(firstParser.Logexpr_statContext ctx) {
        return new ST("<e>;").add("e", visit(ctx.logexpr()).render());
    }

    @Override
    public ST visitComparexpr(firstParser.ComparexprContext ctx) {
        return visit(ctx.comexpr());
    }

    @Override
    public ST visitXexpr(firstParser.XexprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public ST visitVar_decl(firstParser.Var_declContext ctx) {
        String name = ctx.ID().getText();

        if (ctx.expr() != null) {
            return new ST("int <n> = <v>;")
                    .add("n", name)
                    .add("v", visit(ctx.expr()).render());
        }

        return new ST("int <n>;").add("n", name);
    }

    @Override
    public ST visitIf_stat(firstParser.If_statContext ctx) {
        if (ctx.elseBlock != null) {
            return new ST("if (<c>) <t> else <e>")
                    .add("c", visit(ctx.cond).render())
                    .add("t", visit(ctx.thenBlock).render())
                    .add("e", visit(ctx.elseBlock).render());
        }

        return new ST("if (<c>) <t>")
                .add("c", visit(ctx.cond).render())
                .add("t", visit(ctx.thenBlock).render());
    }

    @Override
    public ST visitWhile_stat(firstParser.While_statContext ctx) {
        return new ST("while (<c>) <b>")
                .add("c", visit(ctx.cond).render())
                .add("b", visit(ctx.body).render());
    }

    @Override
    public ST visitPrint_stat(firstParser.Print_statContext ctx) {
        return new ST("print <e>;")
                .add("e", visit(ctx.expr()).render());
    }

    @Override
    public ST visitBlock_single(firstParser.Block_singleContext ctx) {
        return visit(ctx.stat());
    }

    @Override
    public ST visitBlock_real(firstParser.Block_realContext ctx) {
        ST st = new ST("{\n<items; separator=\"\\n\">\n}");
        for (firstParser.StatContext s : ctx.stat()) {
            st.add("items", visit(s).render());
        }
        return st;
    }

    @Override
    public ST visitAssignExpr(firstParser.AssignExprContext ctx) {
        return new ST("<n> = <v>")
                .add("n", ctx.ID().getText())
                .add("v", visit(ctx.expr()).render());
    }

    @Override
    public ST visitOrExpr(firstParser.OrExprContext ctx) {
        return new ST("(<l> or <r>)")
                .add("l", visit(ctx.expr(0)).render())
                .add("r", visit(ctx.expr(1)).render());
    }

    @Override
    public ST visitAndExpr(firstParser.AndExprContext ctx) {
        return new ST("(<l> and <r>)")
                .add("l", visit(ctx.expr(0)).render())
                .add("r", visit(ctx.expr(1)).render());
    }

    @Override
    public ST visitEqExpr(firstParser.EqExprContext ctx) {
        return new ST("(<l> <op> <r>)")
                .add("l", visit(ctx.expr(0)).render())
                .add("op", ctx.getChild(1).getText())
                .add("r", visit(ctx.expr(1)).render());
    }

    @Override
    public ST visitRelExpr(firstParser.RelExprContext ctx) {
        return new ST("(<l> <op> <r>)")
                .add("l", visit(ctx.expr(0)).render())
                .add("op", ctx.getChild(1).getText())
                .add("r", visit(ctx.expr(1)).render());
    }

    @Override
    public ST visitAddExpr(firstParser.AddExprContext ctx) {
        return new ST("(<l> <op> <r>)")
                .add("l", visit(ctx.expr(0)).render())
                .add("op", ctx.getChild(1).getText())
                .add("r", visit(ctx.expr(1)).render());
    }

    @Override
    public ST visitMulExpr(firstParser.MulExprContext ctx) {
        return new ST("(<l> <op> <r>)")
                .add("l", visit(ctx.expr(0)).render())
                .add("op", ctx.getChild(1).getText())
                .add("r", visit(ctx.expr(1)).render());
    }

    @Override
    public ST visitNotExpr(firstParser.NotExprContext ctx) {
        return new ST("(not <e>)")
                .add("e", visit(ctx.expr()).render());
    }

    @Override
    public ST visitParensExpr(firstParser.ParensExprContext ctx) {
        return new ST("(<e>)")
                .add("e", visit(ctx.expr()).render());
    }

    @Override
    public ST visitIntExpr(firstParser.IntExprContext ctx) {
        return new ST("<i>")
                .add("i", ctx.INT().getText());
    }

    @Override
    public ST visitTrueExpr(firstParser.TrueExprContext ctx) {
        return new ST("true");
    }

    @Override
    public ST visitFalseExpr(firstParser.FalseExprContext ctx) {
        return new ST("false");
    }

    @Override
    public ST visitIdExpr(firstParser.IdExprContext ctx) {
        return new ST("<n>")
                .add("n", ctx.ID().getText());
    }
}