package interpreter;

import grammar.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;

import java.util.HashMap;
import java.util.Map;

public class CalculateVisitor extends firstBaseVisitor<Integer> {
    private TokenStream tokStream = null;
    private CharStream input = null;

    // tutaj trzymamy zmienne, np. x = 5
    private final Map<String, Integer> memory = new HashMap<>();

    public CalculateVisitor(CharStream inp) {
        super();
        this.input = inp;
    }

    public CalculateVisitor(TokenStream tok) {
        super();
        this.tokStream = tok;
    }

    public CalculateVisitor(CharStream inp, TokenStream tok) {
        super();
        this.input = inp;
        this.tokStream = tok;
    }

    private String getText(ParserRuleContext ctx) {
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        if (input == null) throw new RuntimeException("Input stream undefined");
        return input.getText(new Interval(a, b));
    }

    @Override
    public Integer visitIf_stat(firstParser.If_statContext ctx) {
        Integer result = 0;

        if (visit(ctx.cond) != 0) {
            result = visit(ctx.thenBlock);
        } else {
            if (ctx.elseBlock != null) {
                result = visit(ctx.elseBlock);
            }
        }

        return result;
    }

    @Override
    public Integer visitWhile_stat(firstParser.While_statContext ctx) {
        Integer result = 0;

        while (visit(ctx.cond) != 0) {
            result = visit(ctx.body);
        }

        return result;
    }

    @Override
    public Integer visitPrint_stat(firstParser.Print_statContext ctx) {
        var st = ctx.expr();
        var result = visit(st);

        System.out.printf("|%s=%d|\n", st.getText(), result);

        return result;
    }

    /*
    @Override
    public Integer visitExpr_stat(firstParser.Expr_statContext ctx) {
        return visit(ctx.logexpr());
    }

     tu cos nie działa idk
     */
    @Override
    public Integer visitVar_decl(firstParser.Var_declContext ctx) {
        String name = ctx.ID().getText();
        Integer value = 0;

        if (ctx.expr() != null) {
            value = visit(ctx.expr());
        }

        memory.put(name, value);
        return value;
    }

    @Override
    public Integer visitBlock_single(firstParser.Block_singleContext ctx) {
        return visit(ctx.stat());
    }

    @Override
    public Integer visitBlock_real(firstParser.Block_realContext ctx) {
        Integer result = 0;

        for (firstParser.StatContext st : ctx.stat()) {
            result = visit(st);
        }

        return result;
    }

    @Override
    public Integer visitAssignExpr(firstParser.AssignExprContext ctx) {
        String name = ctx.ID().getText();
        Integer value = visit(ctx.expr());

        memory.put(name, value);
        return value;
    }

    @Override
    public Integer visitOrExpr(firstParser.OrExprContext ctx) {
        Integer left = visit(ctx.expr(0));
        Integer right = visit(ctx.expr(1));

        return (left != 0 || right != 0) ? 1 : 0;
    }

    @Override
    public Integer visitAndExpr(firstParser.AndExprContext ctx) {
        Integer left = visit(ctx.expr(0));
        Integer right = visit(ctx.expr(1));

        return (left != 0 && right != 0) ? 1 : 0;
    }

    @Override
    public Integer visitEqExpr(firstParser.EqExprContext ctx) {
        Integer left = visit(ctx.expr(0));
        Integer right = visit(ctx.expr(1));

        String op = ctx.getChild(1).getText();

        if (op.equals("==")) {
            return left.equals(right) ? 1 : 0;
        } else {
            return !left.equals(right) ? 1 : 0;
        }
    }

    @Override
    public Integer visitRelExpr(firstParser.RelExprContext ctx) {
        Integer left = visit(ctx.expr(0));
        Integer right = visit(ctx.expr(1));

        String op = ctx.getChild(1).getText();

        switch (op) {
            case "<":
                return (left < right) ? 1 : 0;
            case ">":
                return (left > right) ? 1 : 0;
            case "<=":
                return (left <= right) ? 1 : 0;
            case ">=":
                return (left >= right) ? 1 : 0;
            default:
                throw new RuntimeException("Unknown relation operator: " + op);
        }
    }

    @Override
    public Integer visitAddExpr(firstParser.AddExprContext ctx) {
        Integer left = visit(ctx.expr(0));
        Integer right = visit(ctx.expr(1));

        String op = ctx.getChild(1).getText();

        if (op.equals("+")) {
            return left + right;
        } else {
            return left - right;
        }
    }

    @Override
    public Integer visitMulExpr(firstParser.MulExprContext ctx) {
        Integer left = visit(ctx.expr(0));
        Integer right = visit(ctx.expr(1));

        String op = ctx.getChild(1).getText();

        if (op.equals("*")) {
            return left * right;
        } else {
            if (right == 0) {
                System.err.println("Div by zero");
                throw new ArithmeticException();
            }
            return left / right;
        }
    }

    @Override
    public Integer visitNotExpr(firstParser.NotExprContext ctx) {
        Integer value = visit(ctx.expr());
        return (value == 0) ? 1 : 0;
    }

    @Override
    public Integer visitParensExpr(firstParser.ParensExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Integer visitIntExpr(firstParser.IntExprContext ctx) {
        return Integer.valueOf(ctx.INT().getText());
    }

    @Override
    public Integer visitTrueExpr(firstParser.TrueExprContext ctx) {
        return 1;
    }

    @Override
    public Integer visitFalseExpr(firstParser.FalseExprContext ctx) {
        return 0;
    }

    @Override
    public Integer visitIdExpr(firstParser.IdExprContext ctx) {
        String name = ctx.ID().getText();

        if (!memory.containsKey(name)) {
            throw new RuntimeException("Undefined variable: " + name);
        }

        return memory.get(name);
    }
}