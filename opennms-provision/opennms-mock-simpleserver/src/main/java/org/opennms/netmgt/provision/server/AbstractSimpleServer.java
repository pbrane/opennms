/*
 * Licensed to The OpenNMS Group, Inc (TOG) under one or more
 * contributor license agreements.  See the LICENSE.md file
 * distributed with this work for additional information
 * regarding copyright ownership.
 *
 * TOG licenses this file to You under the GNU Affero General
 * Public License Version 3 (the "License") or (at your option)
 * any later version.  You may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at:
 *
 *      https://www.gnu.org/licenses/agpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.opennms.netmgt.provision.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Abstract AbstractSimpleServer class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
abstract public class AbstractSimpleServer {
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSimpleServer.class);
    
    public static interface RequestMatcher{
        public boolean matches(String input);
    }
    
    public static interface Exchange {
        public boolean sendReply(OutputStream out) throws IOException;
        public boolean processRequest(BufferedReader in) throws IOException;
    }
    
    public static class BannerExchange implements Exchange{
        private String m_banner;
        
        public BannerExchange(String banner){
            m_banner = banner;
        }
        
        @Override
        public boolean processRequest(BufferedReader in) throws IOException { return true; }

        @Override
        public boolean sendReply(OutputStream out) throws IOException {
            out.write(String.format("%s\r%n", m_banner).getBytes());
            return true;
        }
        
    }
    
    public static class SimpleServerExchange implements Exchange{
        private String m_response;
        private RequestMatcher m_requestMatcher;
        
        public SimpleServerExchange(RequestMatcher requestMatcher, String response){
            m_response = response;
            m_requestMatcher = requestMatcher;
        }
        
        @Override
        public boolean processRequest(BufferedReader in) throws IOException {
            String line = in.readLine();
            LOG.info("processing request: {}", line);
            
            if(line == null)return false;
            
            return m_requestMatcher.matches(line);
        }

        @Override
        public boolean sendReply(OutputStream out) throws IOException {
            LOG.info("writing output: {}", m_response);
            out.write(String.format("%s\r%n", m_response).getBytes());
            return false;
        }
        
    }
    
    private ServerSocket m_serverSocket = null;
    private int m_timeout;
    private List<Exchange> m_conversation = new ArrayList<>();
    
    /**
     * <p>getTimeout</p>
     *
     * @return a int.
     */
    public int getTimeout() {
        return m_timeout;
    }
    
    /**
     * <p>setTimeout</p>
     *
     * @param timeout a int.
     */
    public void setTimeout(int timeout) {
        m_timeout = timeout;
    }
    
    /**
     * <p>getInetAddress</p>
     *
     * @return InetAddress returns the inetaddress from the serversocket.
     */
    public InetAddress getInetAddress(){
        return m_serverSocket.getInetAddress();
    }
    
    /**
     * <p>getLocalPort</p>
     *
     * @return a int.
     */
    public int getLocalPort(){
        return m_serverSocket.getLocalPort();
    }
    
    /**
     * <p>init</p>
     *
     * @throws java.lang.Exception if any.
     */
    public final void init() throws Exception{
        m_serverSocket = new ServerSocket();
        m_serverSocket.bind(null);
        onInit();
        startServer();
    }

    /**
     * <p>onInit</p>
     */
    protected void onInit() {
        // Do nothing by default
    } 
    
    /**
     * <p>startServer</p>
     *
     * @throws java.lang.Exception if any.
     */
    public void startServer() throws Exception{
        Thread serverThread = new Thread(getRunnable(), this.getClass().getSimpleName());
        serverThread.start();
    }
    
    /**
     * <p>getRunnable</p>
     *
     * @return a {@link java.lang.Runnable} object.
     * @throws java.lang.Exception if any.
     */
    public Runnable getRunnable() throws Exception{
        return new Runnable(){
            
            @Override
            public void run(){
                try{
                    m_serverSocket.setSoTimeout(getTimeout());
                    Socket socket = m_serverSocket.accept();
                    
                    OutputStream out = socket.getOutputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    
                    attemptConversation(in, out);
                    
                    socket.close();
                }catch(Throwable e){
                    throw new UndeclaredThrowableException(e);
                }
            }
            
        };
    }
    
    /**
     * <p>attemptConversation</p>
     *
     * @param in a {@link java.io.BufferedReader} object.
     * @param out a {@link java.io.OutputStream} object.
     * @return a boolean.
     * @throws java.io.IOException if any.
     */
    protected boolean attemptConversation(BufferedReader in, OutputStream out) throws IOException{
        for(Exchange ex : m_conversation){
            if(!ex.processRequest(in)){
                return false;
            }
            
            if(!ex.sendReply(out)){
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * <p>setExpectedBanner</p>
     *
     * @param banner a {@link java.lang.String} object.
     */
    protected void setExpectedBanner(String banner){
        m_conversation.add(new BannerExchange(banner));
    }
    
    /**
     * <p>addRequestResponse</p>
     *
     * @param request a {@link java.lang.String} object.
     * @param response a {@link java.lang.String} object.
     */
    protected void addRequestResponse(String request, String response){
        m_conversation.add(new SimpleServerExchange(regexpMatches(request), response));
    }
    
    /**
     * <p>regexpMatches</p>
     *
     * @param regex a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.provision.server.AbstractSimpleServer.RequestMatcher} object.
     */
    protected RequestMatcher regexpMatches(final String regex) {
        return new RequestMatcher() {

            @Override
            public boolean matches(String input) {
                return input.matches(regex);
            }
            
        };
    }   
}
