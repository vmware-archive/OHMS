/* ********************************************************************************
 * BoardService_TEST.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *******************************************************************************/

package com.vmware.vrack.hms.testplugin;

import java.util.ArrayList;
import java.util.List;
import com.vmware.vrack.hms.common.boardvendorservice.api.BoardServiceImplementation;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;

/**
 * Test board
 */
@BoardServiceImplementation( name = "TEST" )
public class BoardService_TEST
    extends AbstractTestBoardService
{
    private List<BoardInfo> supportedBoards;

    public BoardService_TEST()
    {
        super();
        BoardInfo boardInfo = new BoardInfo();
        boardInfo.setBoardManufacturer( "TEST_BOARD" );
        boardInfo.setBoardProductName( "TEST_MODEL_001" );
        addSupportedBoard( boardInfo );
    }

    public boolean addSupportedBoard( BoardInfo boardInfo )
    {
        if ( supportedBoards == null )
        {
            supportedBoards = new ArrayList<BoardInfo>();
        }

        return supportedBoards.add( boardInfo );
    }

    @Override
    public List<BoardInfo> getSupportedBoard()
    {

        return supportedBoards;

    }

    @Override
    public boolean setBmcPassword( ServiceHmsNode serviceHmsNode, String username, String newPassword )
        throws HmsException
    {
        return false;
    }

}
