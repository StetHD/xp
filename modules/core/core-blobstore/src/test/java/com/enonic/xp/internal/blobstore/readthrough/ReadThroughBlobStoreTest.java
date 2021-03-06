package com.enonic.xp.internal.blobstore.readthrough;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.util.ByteSizeParser;

import static org.junit.Assert.*;

public class ReadThroughBlobStoreTest
{
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private MemoryBlobStore readThroughStore;

    private MemoryBlobStore finalStore;

    @Before
    public void setUp()
        throws Exception
    {
        this.readThroughStore = new MemoryBlobStore();
        this.finalStore = new MemoryBlobStore();
    }

    @Test
    public void stored_in_readthrough()
        throws Exception
    {
        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( this.readThroughStore ).
            store( this.finalStore ).
            sizeThreshold( ByteSizeParser.parse( "80kb" ) ).
            build();

        final ByteSource binary = ByteSource.wrap( "this is a record".getBytes() );

        final Segment segment = Segment.from( "test" );
        final BlobRecord record = actualBlobStore.addRecord( segment, binary );

        assertNotNull( this.readThroughStore.getRecord( segment, record.getKey() ) );
        assertNotNull( this.finalStore.getRecord( segment, record.getKey() ) );
    }

    @Test
    public void stored_after_read()
        throws Exception
    {
        final ByteSource binary = ByteSource.wrap( "this is a record".getBytes() );
        final Segment segment = Segment.from( "test" );

        final BlobRecord record = this.finalStore.addRecord( segment, binary );

        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( this.readThroughStore ).
            store( this.finalStore ).
            build();

        assertNull( this.readThroughStore.getRecord( segment, record.getKey() ) );

        actualBlobStore.getRecord( segment, record.getKey() );

        assertNotNull( this.readThroughStore.getRecord( segment, record.getKey() ) );
    }

    @Test
    public void obey_size_threshold()
        throws Exception
    {
        final ReadThroughBlobStore actualBlobStore = ReadThroughBlobStore.create().
            readThroughStore( this.readThroughStore ).
            store( this.finalStore ).
            sizeThreshold( ByteSizeParser.parse( "80kb" ) ).
            build();

        final ByteSource binary = ByteSource.wrap( ByteStreams.toByteArray( this.getClass().getResourceAsStream( "fish-86kb.jpg" ) ) );

        final Segment segment = Segment.from( "test" );
        final BlobRecord record = actualBlobStore.addRecord( segment, binary );

        assertNull( this.readThroughStore.getRecord( segment, record.getKey() ) );
        assertNotNull( this.finalStore.getRecord( segment, record.getKey() ) );
    }
}