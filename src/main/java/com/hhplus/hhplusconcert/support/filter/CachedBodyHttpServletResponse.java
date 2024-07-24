package com.hhplus.hhplusconcert.support.filter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream cachedBody;
    private final ServletOutputStream outputStream;
    private final PrintWriter writer;
    private boolean isBodyUsed = false;

    public CachedBodyHttpServletResponse(HttpServletResponse response) {
        super(response);
        this.cachedBody = new ByteArrayOutputStream();
        this.outputStream = new CachedBodyServletOutputStream(this.cachedBody);
        this.writer = new PrintWriter(this.cachedBody, true, StandardCharsets.UTF_8);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        isBodyUsed = true;
        return this.outputStream;
    }

    @Override
    public PrintWriter getWriter() {
        isBodyUsed = true;
        return this.writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (isBodyUsed) {
            if (writer != null) {
                writer.flush();
            }
            if (outputStream != null) {
                outputStream.flush();
            }
            byte[] body = cachedBody.toByteArray();
            getResponse().getOutputStream().write(body);
            getResponse().getOutputStream().flush();
        }
        super.flushBuffer();
    }

    public String getCachedBody() {
        return cachedBody.toString(StandardCharsets.UTF_8);
    }

    private static class CachedBodyServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream outputStream;

        public CachedBodyServletOutputStream(ByteArrayOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            throw new UnsupportedOperationException();
        }
    }
}