/* ********************************************************************************
 * BoardService_TEST.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.testplugin;

import java.util.ArrayList;
import java.util.List;
import com.vmware.vrack.hms.common.boardvendorservice.api.BoardServiceImplementation;
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
}
